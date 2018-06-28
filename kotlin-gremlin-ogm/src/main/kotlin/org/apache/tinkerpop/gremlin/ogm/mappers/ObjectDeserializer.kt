package org.apache.tinkerpop.gremlin.ogm.mappers

import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.ObjectDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.PropertyDescription
import kotlin.reflect.KParameter

internal class ObjectDeserializer<out T : Any>(
        private val graphDescription: GraphDescription,
        private val objectDescription: ObjectDescription<T>,
        private val idProperty: Pair<String, PropertyDescription<T, *>>? = null,
        private val fromVertexParameter: Pair<String, KParameter>? = null,
        private val toVertexParameter: Pair<String, KParameter>? = null
) : Mapper<Map<*, *>, T> {

    override fun invoke(from: Map<*, *>): T {
        val constructorParameters = mutableMapOf<KParameter, Any?>()
        constructorParameters.putAll(objectDescription.properties.entries.associate { keyValue ->
            val propertyKey = keyValue.key
            val propertyDescription = keyValue.value
            val serializedPropertyValue = from[propertyKey]
            val deserializer = PropertyDeserializer(graphDescription, propertyDescription)
            val deserializedPropertyValue = deserializer(serializedPropertyValue)
            propertyDescription.parameter to deserializedPropertyValue
        })
        constructorParameters.putAll(objectDescription.nullConstructorParameters.associate { it to null })
        if (idProperty != null) {
            val id = from[idProperty.first]
            constructorParameters[idProperty.second.parameter] = id
        }
        if (fromVertexParameter != null) {
            val fromVertex = from[fromVertexParameter.first]
            constructorParameters[fromVertexParameter.second] = fromVertex
        }
        if (toVertexParameter != null) {
            val toVertex = from[toVertexParameter.first]
            constructorParameters[toVertexParameter.second] = toVertex
        }
        return objectDescription.constructor.callBy(constructorParameters)
    }
}
