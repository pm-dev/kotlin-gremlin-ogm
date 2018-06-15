package org.apache.tinkerpop.gremlin.ogm.reflection


import org.apache.tinkerpop.gremlin.ogm.annotations.*
import org.apache.tinkerpop.gremlin.ogm.exceptions.*
import org.apache.tinkerpop.gremlin.ogm.extensions.filterNullValues
import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.SerializedProperty
import kotlin.reflect.*
import kotlin.reflect.full.*

/**
 * Describes information about an object that is registered to be persisted to a graph either as a
 * vertex or nested object.
 */
internal open class ObjectDescription<T : Any> (klass: KClass<T>) {

        /**
         * The constructor for the object that can be called with the parameters of the property description's +
         * nullConstructorParameters
         */
        val constructor: KFunction<T> = klass.constructor()

        /**
         * The properties of T that can be mapped to properties of a vertex.
         * The keys of the map are used as keys for the vertex properties.
         */
        val properties: Map<String, PropertyDescription<T>> = klass.properties()

        /**
         * The parameters for the primary constructor that should be called with null as their value.
         * All non-nullable, non-optional parameters will be to the properties map, however, nullable
         * non-optional properties that are not to the properties map (aka transient) must still be passed
         * to the constructor with null as their value.
         */
        val nullConstructorParameters: Collection<KParameter> = constructor.nullParameters()
}

internal fun KParameter.findMapper(): PropertyBiMapper<Any, SerializedProperty>? {
        val mapperAnnotation = findAnnotation<Mapper>() ?: return null
        val mapperInputType = mapperAnnotation.kClass.supertypes.single {
                val mapperAnnotationSuperClass = it.classifier as? KClass<*>
                mapperAnnotationSuperClass != null && mapperAnnotationSuperClass.isSubclassOf(PropertyBiMapper::class)
        }.arguments.first().type
        verifyClassifiersAreCompatible(type.classifier, mapperInputType?.classifier)
        @Suppress("UNCHECKED_CAST")
        return mapperAnnotation.kClass.createInstance() as? PropertyBiMapper<Any, SerializedProperty>
}

private fun <T : Any> KClass<T>.properties(): Map<String, PropertyDescription<T>> {
        val memberProperties = memberProperties
        val annotatedMemberProperties = memberProperties
                .associate { property -> property to property.findAnnotation<Property>() }
                .filterNullValues()
        val memberPropertiesByKey = annotatedMemberProperties.entries.associate { it.value.key to it.key }
        if (memberPropertiesByKey.size != annotatedMemberProperties.size) throw DuplicatePropertyName(this)
        val memberPropertiesByName = memberProperties.associateBy { property -> property.name }

        val constructor = primaryConstructor ?: throw PrimaryConstructorMissing(this)
        val parameters = constructor.parameters
        val parametersToAnnotation = parameters.associate { param -> param to param.findAnnotation<Property>() }.filterNullValues()
        val propertyDescriptionsByKey = parametersToAnnotation.map { (param, annotation) ->
                        val property = memberPropertiesByKey[annotation.key] ?:
                        memberPropertiesByName[param.name] ?:
                        throw ParameterPropertyNotFound(this, annotation, param)
                        if (param.findAnnotation<ToVertex>() != null) throw ConflictingAnnotations(this, param)
                        if (param.findAnnotation<FromVertex>() != null) throw ConflictingAnnotations(this, param)
                        if (param.findAnnotation<ID>() != null) throw ConflictingAnnotations(this, param)
                        annotation.key to PropertyDescription(param, property, param.findMapper())
                }
                .associate { it }
        if (propertyDescriptionsByKey.size != parametersToAnnotation.size) throw DuplicatePropertyName(this)
        return propertyDescriptionsByKey
}

private fun <T : Any> KClass<T>.constructor(): KFunction<T> =
        primaryConstructor ?: throw PrimaryConstructorMissing(this)

private fun KFunction<*>.nullParameters(): List<KParameter> =
        parameters.filter { param ->
                if (!param.isOptional &&
                        param.findAnnotation<ID>() == null &&
                        param.findAnnotation<Property>() == null &&
                        param.findAnnotation<ToVertex>() == null &&
                        param.findAnnotation<FromVertex>() == null) {
                        if (!param.type.isMarkedNullable) throw NonNullableNonOptionalParameter(param)
                        true
                } else {
                        false
                }
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
