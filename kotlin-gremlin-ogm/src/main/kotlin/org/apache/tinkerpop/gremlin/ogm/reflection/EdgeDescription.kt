package org.apache.tinkerpop.gremlin.ogm.reflection

import org.apache.tinkerpop.gremlin.ogm.annotations.*
import org.apache.tinkerpop.gremlin.ogm.exceptions.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation


/**
 * Contains the reflection information needed to map an object to/from an edge.
 */
internal class EdgeDescription<T: Any>(label: String, klass: KClass<T>) : ElementDescription<T>(label, klass) {

    /**
     * The parameter for the 'to' vertex of the edge.
     */
    val toVertex: KParameter = klass.toVertexParameter(constructor)

    /**
     * The property description for the 'from' vertex of the edge.
     */
    val fromVertex: KParameter = klass.fromVertexParameter(constructor)
}

private fun <T : Any> KClass<T>.toVertexParameter(constructor: KFunction<T>): KParameter {
    val annotatedToVertexParams = constructor.parameters.filter { param -> param.findAnnotation<ToVertex>() != null }
    if (annotatedToVertexParams.size > 1) throw DuplicateToVertex(this)
    if (annotatedToVertexParams.isEmpty()) throw ToVertexParameterMissing(this)
    val annotatedToVertexParam = annotatedToVertexParams.single()
    if (annotatedToVertexParam.findAnnotation<Mapper>() != null) throw MapperUnsupported(annotatedToVertexParam)
    if (annotatedToVertexParam.findAnnotation<Property>() != null) throw ConflictingAnnotations(this, annotatedToVertexParam)
    if (annotatedToVertexParam.findAnnotation<FromVertex>() != null) throw ConflictingAnnotations(this, annotatedToVertexParam)
    if (annotatedToVertexParam.findAnnotation<ID>() != null) throw ConflictingAnnotations(this, annotatedToVertexParam)
    return annotatedToVertexParam
}

private fun <T : Any> KClass<T>.fromVertexParameter(constructor: KFunction<T>): KParameter {
    val annotatedFromVertexParams = constructor.parameters.filter { param -> param.findAnnotation<FromVertex>() != null }
    if (annotatedFromVertexParams.size > 1) throw DuplicateFromVertex(this)
    if (annotatedFromVertexParams.size != 1) throw FromVertexParameterMissing(this)
    val annotatedFromVertexParam = annotatedFromVertexParams.single()
    if (annotatedFromVertexParam.findAnnotation<Mapper>() != null) throw MapperUnsupported(annotatedFromVertexParam)
    if (annotatedFromVertexParam.findAnnotation<Property>() != null) throw ConflictingAnnotations(this, annotatedFromVertexParam)
    if (annotatedFromVertexParam.findAnnotation<ToVertex>() != null) throw ConflictingAnnotations(this, annotatedFromVertexParam)
    if (annotatedFromVertexParam.findAnnotation<ID>() != null) throw ConflictingAnnotations(this, annotatedFromVertexParam)
    return annotatedFromVertexParam
}
