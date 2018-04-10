package org.apache.tinkerpop.gremlin.ogm.reflection

import org.apache.tinkerpop.gremlin.ogm.annotations.Vertex
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

/**
 * Contains the reflection information needed to map an object to/from a vertex.
 */
internal class VertexObjectDescription<T : Any>(

        /**
         * The label of the vertex as stored in the graph
         */
        val label: String,

        /**
         * The property description for the id of the vertex
         */
        val id: PropertyDescription<T>,
        properties: Map<String, PropertyDescription<T>>,
        constructor: KFunction<T>,
        nullConstructorParameters: Collection<KParameter>
) : ObjectDescription<T>(
        properties = properties,
        constructor = constructor,
        nullConstructorParameters = nullConstructorParameters
) {

    companion object {
        fun <T : Any> describe(kClass: KClass<T>): VertexObjectDescription<T> {
            val label = kClass.findAnnotation<Vertex>()?.label
                    ?: throw RuntimeException("Class must be annotated with Vertex to be mapped to gremlin: $kClass")
            val built = buildObjectDescription(kClass = kClass, includeIDDescription = true)
            return VertexObjectDescription(
                    label,
                    built.idDescription!!,
                    built.objectDescription.properties,
                    built.objectDescription.constructor,
                    built.objectDescription.nullConstructorParameters)
        }
    }
}
