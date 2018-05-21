package org.apache.tinkerpop.gremlin.ogm.reflection

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.BaseEdge
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter


/**
 * Contains the reflection information needed to map an object to/from an edge.
 */
internal class EdgeDescription<T: Any>(

        /**
         * The parameter for the 'in' vertex of the edge.
         */
        val inVertex: KParameter,

        /**
         * The property description for the 'out' vertex of the edge.
         */
        val outVertex: KParameter,

        label: String,
        id: PropertyDescription<T>,
        properties: Map<String, PropertyDescription<T>>,
        constructor: KFunction<T>,
        nullConstructorParameters: Collection<KParameter>
) : ElementDescription<T>(
        label,
        id,
        properties,
        constructor,
        nullConstructorParameters
) {
    companion object {
        fun <E : BaseEdge<*, *>> describe(relationship: Relationship<*, *>, kClass: KClass<E>): EdgeDescription<E> {
            val built = buildObjectDescription(kClass = kClass, type = ObjectDescriptionType.Edge)
            return EdgeDescription(
                    built.inVertexParameter!!,
                    built.outVertexParameter!!,
                    relationship.name,
                    built.idDescription!!,
                    built.objectDescription.properties,
                    built.objectDescription.constructor,
                    built.objectDescription.nullConstructorParameters)
        }
    }
}
