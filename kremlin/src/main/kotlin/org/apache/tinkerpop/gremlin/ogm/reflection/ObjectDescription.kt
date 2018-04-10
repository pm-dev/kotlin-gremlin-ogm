package org.apache.tinkerpop.gremlin.ogm.reflection


import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

/**
 * Describes information about an object that is registered to be persisted in a graph either as a
 * vertex or nested object.
 */
internal open class ObjectDescription<T : Any> (

        /**
         * The properties of T that can be mapped to properties of a vertex.
         * The keys of the map are used as keys for the vertex properties.
         */
        val properties: Map<String, PropertyDescription<T>>,

        /**
         * The constructor for the object that can be called with the parameters of the property description's +
         * nullConstructorParameters
         */
        val constructor: KFunction<T>,

        /**
         * The parameters for the primary constructor that should be called with null as their value.
         * All non-nullable, non-optional parameters will be in the properties map, however, nullable
         * non-optional properties that are not in the properties map (aka transient) must still be passed
         * to the constructor with null as their value.
         */
        val nullConstructorParameters: Collection<KParameter>
) {
        companion object {
                fun <T: Any> describe(kClass: KClass<T>): ObjectDescription<T> =
                        buildObjectDescription(kClass).objectDescription
        }
}
