package org.apache.tinkerpop.gremlin.ogm.reflection

import org.apache.tinkerpop.gremlin.ogm.GraphMapper.Companion.idTag
import org.apache.tinkerpop.gremlin.ogm.annotations.ID
import org.apache.tinkerpop.gremlin.ogm.annotations.Mapper
import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import org.apache.tinkerpop.gremlin.ogm.exceptions.*
import org.apache.tinkerpop.gremlin.ogm.extensions.nestedPropertyDelimiter
import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.SerializedProperty
import kotlin.reflect.*
import kotlin.reflect.full.*

internal class BuiltObjectDescription<T : Any>(
        val objectDescription: ObjectDescription<T>,
        val idDescription: PropertyDescription<T>?)

internal fun <T : Any> buildObjectDescription(
        kClass: KClass<T>,
        includeIDDescription: Boolean = false
): BuiltObjectDescription<T> {
    val constructor: KFunction<T> = kClass.primaryConstructor ?: throw PrimaryConstructorMissing(kClass)

    // Parse parameters
    var idParameter: KParameter? = null
    val annotatedParameters = mutableListOf<Pair<String, Pair<KParameter, PropertyBiMapper<Any, SerializedProperty>?>>>()
    val nullParameters = mutableListOf<KParameter>()
    for (parameter in constructor.parameters) {
        val parameterAnnotation = parameter.findAnnotation<Property>()
        if (parameterAnnotation != null) {
            val mapperAnnotation = parameter.findAnnotation<Mapper>()
            if (mapperAnnotation != null) {
                val mapperInputType = mapperAnnotation.kClass.supertypes.single {
                    val mapperAnnotationSuperClass = it.classifier as? KClass<*>
                    mapperAnnotationSuperClass != null && mapperAnnotationSuperClass.isSubclassOf(PropertyBiMapper::class)
                }.arguments.first().type
                verifyClassifiersAreCompatible(parameter.type.classifier, mapperInputType?.classifier)
            }
            @Suppress("UNCHECKED_CAST")
            val customMapper = mapperAnnotation?.kClass?.createInstance() as? PropertyBiMapper<Any, SerializedProperty>
            annotatedParameters.add(parameterAnnotation.key to (parameter to customMapper))
        }
        val idAnnotation = parameter.findAnnotation<ID>()
        if (idAnnotation != null) {
            val idParameterCopy = idParameter
            if (parameterAnnotation != null) throw IDAndProperty(kClass, parameter.name, AnnotationType.parameter)
            if (idParameterCopy != null) throw DuplicateID(kClass, parameter.name, idParameterCopy.name, AnnotationType.parameter)
            if (!parameter.type.isMarkedNullable) throw NonNullableID(kClass, parameter.name, AnnotationType.parameter)
            if (parameter.findAnnotation<Mapper>() != null) throw IDMapperUnsupported(parameter)
            @Suppress("UNCHECKED_CAST")
            idParameter = parameter
        }
        if (parameterAnnotation == null && idAnnotation == null && !parameter.isOptional) {
            nullParameters.add(parameter)
            if (!parameter.type.isMarkedNullable) throw NonNullableNonOptionalParameter(kClass, parameter)
        }
    }
    if (idParameter == null && includeIDDescription) throw IDParameterMissing(kClass, AnnotationType.parameter)

    // Parse properties
    var idProperty: KProperty1<T, *>? = null
    val annotatedProperties = mutableListOf<Pair<String, KProperty1<T, *>>>()
    for (property in kClass.memberProperties) {
        val propertyAnnotation = property.findAnnotation<Property>()
        if (propertyAnnotation != null) {
            annotatedProperties.add(propertyAnnotation.key to property)
        }
        val idAnnotation = property.findAnnotation<ID>()
        if (idAnnotation != null) {
            val idPropertyCopy = idProperty
            if (propertyAnnotation != null) throw IDAndProperty(kClass, property.name, AnnotationType.property)
            if (idPropertyCopy != null) throw DuplicateID(kClass, property.name, idPropertyCopy.name, AnnotationType.property)
            if (!property.returnType.isMarkedNullable) throw NonNullableID(kClass, property.name, AnnotationType.property)
            idProperty = property
        }
    }
    if (idProperty == null && includeIDDescription) throw IDParameterMissing(kClass, AnnotationType.property)


    val parametersMap: Map<String, Pair<KParameter, PropertyBiMapper<Any, SerializedProperty>?>> = annotatedParameters.associate { it }
    val propertyMap: Map<String, KProperty1<T, *>> = annotatedProperties.associate { it }

    if (annotatedParameters.size != parametersMap.size) throw DuplicatePropertyName(kClass, AnnotationType.parameter)
    if (annotatedProperties.size != propertyMap.size) throw DuplicatePropertyName(kClass, AnnotationType.property)

    val propertyDescriptions = parametersMap.keys.union(propertyMap.keys).associate { key ->
        if (key.isEmpty()) throw EmptyPropertyName(kClass)
        if (key == idTag) throw ReservedIDName(kClass)
        if (key.contains(nestedPropertyDelimiter)) throw ReservedNestedPropertyDelimiter(kClass, key)
        if (key.toIntOrNull() != null) throw ReservedNumberKey(kClass, key)
        val parameter = parametersMap[key] ?: throw PropertyMissingOnParameter(kClass, key)
        val property = propertyMap[key] ?: throw PropertyMissingOnProperty(kClass, key)
        val propertyDescription = PropertyDescription(parameter.first, property, parameter.second)
        key to propertyDescription
    }
    val idPropertyDescription = if (includeIDDescription) PropertyDescription(idParameter!!, idProperty!!, null) else null
    val nullConstructorParameters: List<KParameter> = nullParameters.toList()
    return BuiltObjectDescription(
            objectDescription = ObjectDescription(propertyDescriptions, constructor, nullConstructorParameters),
            idDescription = idPropertyDescription)
}

private fun verifyClassifiersAreCompatible(lowerBound: KClassifier?, upperBound: KClassifier?) {
    if (lowerBound == null || upperBound == null) {
        throw ClassifierUnavailable()
    }
    val lowerAsKClass = lowerBound as? KClass<*>
    val upperAsKClass = upperBound as? KClass<*>
    if (lowerAsKClass == null) {
        val lowerAsTypeParameter = lowerBound as KTypeParameter
        lowerAsTypeParameter.upperBounds.forEach {
            verifyClassifiersAreCompatible(it.classifier, upperBound)
        }
    } else if (upperAsKClass == null) {
        val upperAsTypeParameter = upperBound as KTypeParameter
        upperAsTypeParameter.upperBounds.forEach {
            verifyClassifiersAreCompatible(lowerBound, it.classifier)
        }
    } else if (!lowerAsKClass.isSubclassOf(upperBound)) {
        throw ClassInheritanceMismatch(lowerBound, upperBound)
    }
}
