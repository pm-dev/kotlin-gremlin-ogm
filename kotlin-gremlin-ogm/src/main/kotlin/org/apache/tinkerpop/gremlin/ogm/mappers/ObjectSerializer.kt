package org.apache.tinkerpop.gremlin.ogm.mappers

import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.ObjectDescription

internal class ObjectSerializer<in T : Any>(
        private val graphDescription: GraphDescription,
        private val objectDescription: ObjectDescription<T>
) : Mapper<T, Map<*, *>> {

    override fun invoke(from: T): Map<String, SerializedProperty?> =
            objectDescription.properties.mapValues {
                val propertyDescription = it.value
                val unserializedPropertyValue = propertyDescription.property.get(from)
                val serializer = PropertySerializer(graphDescription, propertyDescription)
                serializer(unserializedPropertyValue)
            }
}
