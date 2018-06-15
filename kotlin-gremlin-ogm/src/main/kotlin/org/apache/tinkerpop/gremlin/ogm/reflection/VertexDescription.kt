package org.apache.tinkerpop.gremlin.ogm.reflection

import org.apache.tinkerpop.gremlin.ogm.annotations.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.VertexAnnotationMissing
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Contains the reflection information needed to map an object to/from a vertex.
 */
internal class VertexDescription<T : Any>(klass: KClass<T>) : ElementDescription<T>(klass.label(), klass)

private fun <T : Any> KClass<T>.label(): String =
        findAnnotation<Vertex>()?.label ?: throw VertexAnnotationMissing(this)

