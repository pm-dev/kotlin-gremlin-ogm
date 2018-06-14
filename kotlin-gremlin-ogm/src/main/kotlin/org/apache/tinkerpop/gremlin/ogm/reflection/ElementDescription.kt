package org.apache.tinkerpop.gremlin.ogm.reflection

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter


/**
 * Contains the reflection information needed to map an object to/from a graph element
 * (vertex or graph).
 */
internal abstract class ElementDescription<T : Any>(

        /**
         * The label of the element as stored to the graph
         */
        val label: String,

        /**
         * The property description for the id of the element
         */
        val id: PropertyDescription<T>,

        properties: Map<String, PropertyDescription<T>>,
        constructor: KFunction<T>,
        nullConstructorParameters: Collection<KParameter>
) : ObjectDescription<T>(
        properties,
        constructor,
        nullConstructorParameters
)
