package org.apache.tinkerpop.gremlin.ogm.reflection

import org.apache.tinkerpop.gremlin.ogm.annotations.*
import org.apache.tinkerpop.gremlin.ogm.exceptions.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties


/**
 * Contains the reflection information needed to map an object to/from a graph element
 * (vertex or graph).
 */
internal abstract class ElementDescription<T : Any>(

        /**
         * The label of the element as stored to the graph
         */
        val label: String,

        klass: KClass<T>

) : ObjectDescription<T>(klass) {

    /**
     * The property description for the id of the element
     */
    val id: PropertyDescription<T> = klass.idPropertyDescription(constructor)
}

private fun <T : Any> KClass<T>.idPropertyDescription(constructor: KFunction<T>): PropertyDescription<T> {
    val memberProperties = memberProperties
    val memberPropertiesByName = memberProperties.associateBy { property -> property.name }
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
    if (!annotatedIDParam.type.isMarkedNullable) throw NonNullableID(this, annotatedIDParam)
    return PropertyDescription(annotatedIDParam, idProperty, annotatedIDParam.findMapper())
}
