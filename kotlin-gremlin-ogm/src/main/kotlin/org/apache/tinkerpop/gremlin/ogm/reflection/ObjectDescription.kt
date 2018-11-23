package org.apache.tinkerpop.gremlin.ogm.reflection


import org.apache.tinkerpop.gremlin.ogm.annotations.*
import org.apache.tinkerpop.gremlin.ogm.annotations.defaults.*
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Element
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.*
import org.apache.tinkerpop.gremlin.ogm.extensions.filterNullValues
import org.apache.tinkerpop.gremlin.ogm.extensions.nestedPropertyDelimiter
import org.apache.tinkerpop.gremlin.ogm.mappers.EdgeDeserializer.Companion.idTag
import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.SerializedProperty
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import java.util.function.Supplier
import kotlin.reflect.*
import kotlin.reflect.full.*

/**
 * Describes information about an object that is registered to be persisted to a graph either as a
 * vertex, edge or nested object.
 */
sealed class ObjectDescription<T : Any>(kClass: KClass<T>) {

    /**
     * The constructor for the object that can be called with the parameters of the property description's +
     * nullConstructorParameters
     */
    val constructor: KFunction<T> = kClass.constructor()

    /**
     * The properties of T that can be mapped to properties of a vertex.
     * The keys of the map are used as keys for the vertex properties.
     */
    val properties: Map<String, PropertyDescription<T, *>> = kClass.properties()

    /**
     * The parameters for the primary constructor that should be called with null as their value.
     * All non-nullable, non-optional parameters will be to the properties map, however, nullable
     * non-optional properties that are not to the properties map (aka transient) must still be passed
     * to the constructor with null as their value.
     */
    val nullConstructorParameters: Collection<KParameter> = constructor.nullParameters()
}

/**
 * Contains the reflection information needed to map an object to/from a graph element (vertex or edge).
 */
sealed class ElementDescription<T : Element>(kClass: KClass<T>) : ObjectDescription<T>(kClass) {

    /**
     * The label of the element as stored to the graph
     */
    val label: String = kClass.label()

    /**
     * The property description for the id of the element
     */
    val id: PropertyDescription<T, *> = kClass.idPropertyDescription(constructor)
}

/**
 * Contains the reflection information needed to map an object to/from a vertex.
 */
class VertexDescription<T : Vertex>(kClass: KClass<T>) : ElementDescription<T>(kClass)

/**
 * Contains the reflection information needed to map an object to/from an edge.
 */
class EdgeDescription<out FROM : Vertex, out TO : Vertex, T : Edge<FROM, TO>>(

        /**
         * The relationship for this edge
         */
        val relationship: Relationship<out FROM, out TO>,

        kClass: KClass<T>

) : ElementDescription<T>(kClass) {

    /**
     * The parameter for the 'to' vertex of the edge.
     */
    val toVertex: KParameter = kClass.toVertexPropertyDescription(constructor)

    /**
     * The property description for the 'from' vertex of the edge.
     */
    val fromVertex: KParameter = kClass.fromVertexPropertyDescription(constructor)
}

/**
 * Describes information about an object that is registered to be persisted to a graph as a nested object.
 */
class ObjectPropertyDescription<T : Any>(kClass: KClass<T>) : ObjectDescription<T>(kClass)

private fun <T : Any> KClass<T>.constructor(): KFunction<T> = primaryConstructor
        ?: throw PrimaryConstructorMissing(this)

private fun <T : Any> KClass<T>.label(): String = findAnnotation<org.apache.tinkerpop.gremlin.ogm.annotations.Element>()?.label
        ?: throw ElementAnnotationMissing(this)

private fun <FROM : Vertex, TO : Vertex, T : Edge<FROM, TO>> KClass<T>.toVertexPropertyDescription(constructor: KFunction<T>): KParameter {
    val annotatedToVertexParams = constructor.parameters.filter { param -> param.findAnnotation<ToVertex>() != null }
    if (annotatedToVertexParams.size > 1) throw DuplicateToVertex(this)
    if (annotatedToVertexParams.isEmpty()) throw ToVertexParameterMissing(this)
    val annotatedToVertexParam = annotatedToVertexParams.single()
    if (annotatedToVertexParam.findAnnotation<Mapper>() != null) throw MapperUnsupported(annotatedToVertexParam)
    if (annotatedToVertexParam.findAnnotation<Property>() != null) throw ConflictingAnnotations(this, annotatedToVertexParam)
    if (annotatedToVertexParam.findAnnotation<FromVertex>() != null) throw ConflictingAnnotations(this, annotatedToVertexParam)
    if (annotatedToVertexParam.findAnnotation<ID>() != null) throw ConflictingAnnotations(this, annotatedToVertexParam)
    if (annotatedToVertexParam.type.isMarkedNullable) throw NullableVertexParam(this, annotatedToVertexParam)
    return annotatedToVertexParam
}

private fun <FROM : Vertex, TO : Vertex, T : Edge<FROM, TO>> KClass<T>.fromVertexPropertyDescription(constructor: KFunction<T>): KParameter {
    val annotatedFromVertexParams = constructor.parameters.filter { param -> param.findAnnotation<FromVertex>() != null }
    if (annotatedFromVertexParams.size > 1) throw DuplicateFromVertex(this)
    if (annotatedFromVertexParams.size != 1) throw FromVertexParameterMissing(this)
    val annotatedFromVertexParam = annotatedFromVertexParams.single()
    if (annotatedFromVertexParam.findAnnotation<Mapper>() != null) throw MapperUnsupported(annotatedFromVertexParam)
    if (annotatedFromVertexParam.findAnnotation<Property>() != null) throw ConflictingAnnotations(this, annotatedFromVertexParam)
    if (annotatedFromVertexParam.findAnnotation<ToVertex>() != null) throw ConflictingAnnotations(this, annotatedFromVertexParam)
    if (annotatedFromVertexParam.findAnnotation<ID>() != null) throw ConflictingAnnotations(this, annotatedFromVertexParam)
    if (annotatedFromVertexParam.type.isMarkedNullable) throw NullableVertexParam(this, annotatedFromVertexParam)
    return annotatedFromVertexParam
}

private fun <T : Any> KClass<T>.idPropertyDescription(constructor: KFunction<T>): PropertyDescription<T, *> {
    val memberProperties = memberProperties
    val memberPropertiesByName = memberProperties.associateBy(KProperty1<T, *>::name)
    val annotatedIDProperties = memberProperties.filter { property -> property.findAnnotation<ID>() != null }
    if (annotatedIDProperties.size > 1) throw DuplicateIDProperty(this)
    val annotatedIDParams = constructor.parameters.filter { param -> param.findAnnotation<ID>() != null }
    if (annotatedIDParams.size != 1) throw IDParameterRequired(this)
    val annotatedIDParam = annotatedIDParams.single()
    val idProperty = annotatedIDProperties.singleOrNull()
            ?: memberPropertiesByName[annotatedIDParam.name]
            ?: throw IDPropertyRequired(this)
    if (annotatedIDParam.findAnnotation<Mapper>() != null) throw MapperUnsupported(annotatedIDParam)
    if (annotatedIDParam.findAnnotation<Property>() != null) throw ConflictingAnnotations(this, annotatedIDParam)
    if (annotatedIDParam.findAnnotation<ToVertex>() != null) throw ConflictingAnnotations(this, annotatedIDParam)
    if (annotatedIDParam.findAnnotation<FromVertex>() != null) throw ConflictingAnnotations(this, annotatedIDParam)
    if (annotatedIDParam.findAnnotation<DefaultValue>() != null) throw ConflictingAnnotations(this, annotatedIDParam)
    if (!annotatedIDParam.type.isMarkedNullable) throw NonNullableID(this, annotatedIDParam)
    return PropertyDescription(annotatedIDParam, idProperty, null, null)
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

internal fun KParameter.findDefault(): Supplier<out Any>? {
    findAnnotation<DefaultBoolean>()?.let {
        verifyClassifiersAreCompatible(type.classifier, Boolean::class)
        if (type.isMarkedNullable) throw NullablePropertyWithDefault(this)
        return Supplier(it::value)
    }
    findAnnotation<DefaultByte>()?.let {
        verifyClassifiersAreCompatible(type.classifier, Byte::class)
        if (type.isMarkedNullable) throw NullablePropertyWithDefault(this)
        return Supplier(it::value)
    }
    findAnnotation<DefaultChar>()?.let {
        verifyClassifiersAreCompatible(type.classifier, Char::class)
        if (type.isMarkedNullable) throw NullablePropertyWithDefault(this)
        return Supplier(it::value)
    }
    findAnnotation<DefaultDouble>()?.let {
        verifyClassifiersAreCompatible(type.classifier, Double::class)
        if (type.isMarkedNullable) throw NullablePropertyWithDefault(this)
        return Supplier(it::value)
    }
    findAnnotation<DefaultFloat>()?.let {
        verifyClassifiersAreCompatible(type.classifier, Float::class)
        if (type.isMarkedNullable) throw NullablePropertyWithDefault(this)
        return Supplier(it::value)
    }
    findAnnotation<DefaultInt>()?.let {
        verifyClassifiersAreCompatible(type.classifier, Int::class)
        if (type.isMarkedNullable) throw NullablePropertyWithDefault(this)
        return Supplier(it::value)
    }
    findAnnotation<DefaultLong>()?.let {
        verifyClassifiersAreCompatible(type.classifier, Long::class)
        if (type.isMarkedNullable) throw NullablePropertyWithDefault(this)
        return Supplier(it::value)
    }
    findAnnotation<DefaultShort>()?.let {
        verifyClassifiersAreCompatible(type.classifier, Short::class)
        if (type.isMarkedNullable) throw NullablePropertyWithDefault(this)
        return Supplier(it::value)
    }
    findAnnotation<DefaultString>()?.let {
        verifyClassifiersAreCompatible(type.classifier, String::class)
        if (type.isMarkedNullable) throw NullablePropertyWithDefault(this)
        return Supplier(it::value)
    }
    val annotation = findAnnotation<DefaultValue>() ?: return null
    val suppliedType = annotation.supplier.supertypes.single {
        val annotationSuperClass = it.classifier as? KClass<*>
        annotationSuperClass != null && annotationSuperClass.isSubclassOf(Supplier::class)
    }.arguments.single().type
    verifyClassifiersAreCompatible(type.classifier, suppliedType?.classifier)
    if (type.isMarkedNullable) throw NullablePropertyWithDefault(this)
    return annotation.supplier.createInstance()
}

private fun <T : Any> KClass<T>.properties(): Map<String, PropertyDescription<T, *>> {
    val memberProperties = memberProperties
    val annotatedMemberProperties = memberProperties
            .associate { property -> property to property.findAnnotation<Property>() }
            .filterNullValues()
    val memberPropertiesByKey = annotatedMemberProperties.entries.associate { it.value.key to it.key }
    if (memberPropertiesByKey.size != annotatedMemberProperties.size) throw DuplicatePropertyName(this)
    val memberPropertiesByName = memberProperties.associateBy(KProperty1<T, *>::name)

    val parameters = constructor().parameters
    val parametersToAnnotation = parameters.associate { param -> param to param.findAnnotation<Property>() }.filterNullValues()
    val propertyDescriptionsByKey = parametersToAnnotation.map { (param, annotation) ->
        val property = memberPropertiesByKey[annotation.key] ?: memberPropertiesByName[param.name]
        ?: throw ParameterPropertyNotFound(this, annotation, param)
        if (param.findAnnotation<ToVertex>() != null) throw ConflictingAnnotations(this, param)
        if (param.findAnnotation<FromVertex>() != null) throw ConflictingAnnotations(this, param)
        if (param.findAnnotation<ID>() != null) throw ConflictingAnnotations(this, param)
        if (annotation.key == idTag) throw ReservedIDName(this)
        if (annotation.key.contains(nestedPropertyDelimiter)) throw ReservedNestedPropertyDelimiter(this, annotation.key)
        if (annotation.key.toIntOrNull() != null) throw ReservedNumberKey(this, annotation.key)
        annotation.key to PropertyDescription(param, property, param.findMapper(), param.findDefault())
    }.associate { it }
    if (propertyDescriptionsByKey.size != parametersToAnnotation.size) throw DuplicatePropertyName(this)
    return propertyDescriptionsByKey
}

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
