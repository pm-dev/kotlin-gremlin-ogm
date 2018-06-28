package org.apache.tinkerpop.gremlin.ogm.mappers

import org.apache.tinkerpop.gremlin.ogm.exceptions.IncompatibleIterable
import org.apache.tinkerpop.gremlin.ogm.exceptions.IncompatibleMap
import org.apache.tinkerpop.gremlin.ogm.exceptions.IterableNotSupported
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.PropertyDescription
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

internal class PropertyDeserializer<out T>(
        private val graphDescription: GraphDescription,
        private val propertyDescription: PropertyDescription<T, *>
) : Mapper<SerializedProperty?, Any?> {

    override fun invoke(from: SerializedProperty?): Any? {
        if (propertyDescription.mapper != null && from != null) {
            return propertyDescription.mapper.inverseMap(from)
        }
        return when (from) {
            null -> null
            is Iterable<*> -> {
                val toClass by lazy {
                    propertyDescription.property.returnType.arguments.single().type?.classifier as? KClass<out Any>
                            ?: throw IncompatibleIterable(propertyDescription)
                }
                when {
                    propertyDescription.kClass.isSubclassOf(Set::class) -> from.map { deserializeProperty(it, toClass) }.toSet()
                    propertyDescription.kClass.isSubclassOf(List::class) -> from.map { deserializeProperty(it, toClass) }
                    else -> throw IterableNotSupported(propertyDescription.kClass)
                }
            }
            is Map<*, *> -> {
                if (propertyDescription.kClass.isSubclassOf(Map::class)) {
                    val mapTypeParameters = propertyDescription.property.returnType.arguments
                    val keyClass by lazy {
                        mapTypeParameters.first().type?.classifier as? KClass<out Any>
                                ?: throw IncompatibleMap(propertyDescription)
                    }
                    val valueClass by lazy {
                        mapTypeParameters.last().type?.classifier as? KClass<out Any>
                                ?: throw IncompatibleMap(propertyDescription)
                    }
                    from.entries.associate { deserializeProperty(it.key, keyClass) to deserializeProperty(it.value, valueClass) }
                } else {
                    deserializeProperty(from, propertyDescription.kClass)
                }
            }
            else -> deserializeProperty(from, propertyDescription.kClass)
        }
    }

    private fun deserializeProperty(property: SerializedProperty?, deserializedClass: KClass<out Any>): Any? = when (property) {
        null -> null
        is Map<*, *> -> {
            val description = graphDescription.getObjectPropertyDescription(deserializedClass)
            val deserializer = ObjectDeserializer(graphDescription, description)
            deserializer(property)
        }
        else -> graphDescription.getScalarPropertyMapper(deserializedClass).inverseMap(property)
    }
}
