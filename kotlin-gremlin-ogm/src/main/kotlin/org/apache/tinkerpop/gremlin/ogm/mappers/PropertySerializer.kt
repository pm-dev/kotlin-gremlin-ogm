package org.apache.tinkerpop.gremlin.ogm.mappers

import org.apache.tinkerpop.gremlin.ogm.exceptions.IncompatibleIterable
import org.apache.tinkerpop.gremlin.ogm.exceptions.IncompatibleMap
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.PropertyDescription
import kotlin.reflect.KClass

internal class PropertySerializer<in T>(
        private val graphDescription: GraphDescription,
        private val propertyDescription: PropertyDescription<*, T>
) : Mapper<Any?, SerializedProperty?> {

    override fun invoke(from: Any?): SerializedProperty? {
        if (propertyDescription.mapper != null && from != null) {
            return propertyDescription.mapper.forwardMap(from)
        }
        return when (from) {
            null -> null
            is Iterable<*> -> {
                val fromClass by lazy {
                    propertyDescription.property.returnType.arguments.single().type?.classifier as? KClass<out Any>
                            ?: throw IncompatibleIterable(propertyDescription)
                }
                from.map {
                    serializeProperty(it, fromClass)
                }
            }
            is Map<*, *> -> {
                val mapTypeParameters by lazy { propertyDescription.property.returnType.arguments }
                val keyClass by lazy {
                    mapTypeParameters.first().type?.classifier as? KClass<out Any>
                            ?: throw IncompatibleMap(propertyDescription)
                }
                val valueClass by lazy {
                    mapTypeParameters.last().type?.classifier as? KClass<out Any>
                            ?: throw IncompatibleMap(propertyDescription)
                }
                from.entries.associate {
                    serializeProperty(it.key, keyClass) to serializeProperty(it.value, valueClass)
                }
            }
            else -> serializeProperty(from, propertyDescription.kClass)
        }
    }

    private fun serializeProperty(property: Any?, deserializedClass: KClass<out Any>): SerializedProperty? {
        if (property == null) {
            return null
        }
        if (graphDescription.scalarPropertyClasses.contains(deserializedClass)) {
            return graphDescription.getScalarPropertyMapper(deserializedClass).forwardMap(property)
        }
        val description = graphDescription.getObjectPropertyDescription(deserializedClass)
        val serializer = ObjectSerializer(graphDescription, description)
        return serializer(property)
    }
}
