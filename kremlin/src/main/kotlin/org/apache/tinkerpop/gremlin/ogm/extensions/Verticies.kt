package org.apache.tinkerpop.gremlin.ogm.extensions

import org.apache.tinkerpop.gremlin.ogm.exceptions.EmptyListTokenIsReserved
import org.apache.tinkerpop.gremlin.ogm.mappers.SerializedProperty
import org.apache.tinkerpop.gremlin.structure.Vertex


internal fun Vertex.getProperties(): Map<String, SerializedProperty> {
    val map = mutableMapOf<String, SerializedProperty>()
    properties<Any>().forEach { property -> map.addProperty(property.key(), property.value()) }
    map.mapValuesInPlace { it.value.listify() }
    return map
}

internal fun Vertex.setProperties(
        newProperties: Map<String, SerializedProperty?>
): Vertex {
    properties<Any>().forEach { it.remove() }
    newProperties.forEach { key, value ->
        if (value == emptyListToken) throw EmptyListTokenIsReserved(emptyListToken)
        setProperty(key, value)
    }
    return this
}


/**
 * This is internal and not private so we can check for invalid @Property.name values containing '.', which is invalid.
 */
internal const val nestedPropertyDelimiter = '.'

/**
 *
 * When we encounter an empty list, it's important to saveV a property value that represents an empty list,
 * rather than not saving anything, otherwise, when reading the value for that property later, it would appear that the
 * value is null, which is different from an empty list. This token is what we'll saveV to mark the property value
 * as an empty list. This value must never be changed after it's used in a graph.
 *
 * This is internal and not private for testing.
 */
internal const val emptyListToken = "474A56F1-6309-41B5-A632-AD53F57DBDAE"


/**
 * Whereas SerializedProperty can be any type accepted by the underlying graph implementation PLUS
 * List<SerializedProperty> and Map<String, SerializedProperty>,
 *
 * SerializedScalarProperty represents a SerializedProperty that is not a List or Map.
 */
private typealias SerializedScalarProperty = Any


/**
 * If this property is a map keyed by integer strings, convert it to a list sorted by those integer keys,
 * otherwise return this. If this property is a map, it first recursively calls listify on its values.
 */
private fun SerializedProperty.listify(): SerializedProperty =
        when (this) {
            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                this as MutableMap<String, SerializedProperty>
                this.mapValuesInPlace { it.value.listify() }
                this.toList() ?: this
            }
            emptyListToken -> emptyList<Any>()
            else -> this
        }

/**
 * If every key in the map is an integer string, convert the map to a list, sorted by the integer value of the keys.
 * If any key is not a valid representation of an integer, return null.
 */
private fun Map<String, SerializedProperty>.toList(): List<SerializedProperty>? {
    val indexToValue = mutableListOf<Pair<Int, SerializedProperty>>()
    for (entry in entries) {
        val index = entry.key.toIntOrNull() ?: return null
        indexToValue.add(index to entry.value)
    }
    indexToValue.sortBy { it.first }
    return indexToValue.map { it.second }
}


private fun MutableMap<String, SerializedProperty>.addProperty(propertyKey: String, propertyValue: SerializedScalarProperty) {
    val delimiterIndex = propertyKey.indexOf(nestedPropertyDelimiter)
    when (delimiterIndex) {
        -1 -> put(propertyKey, propertyValue)
        else -> {
            val firstKeyPart = propertyKey.substring(0, delimiterIndex)
            val nextKeyParts = propertyKey.substring(delimiterIndex + 1)
            @Suppress("UNCHECKED_CAST")
            val map = get(firstKeyPart) as MutableMap<String, SerializedProperty>? ?: {
                val newMap = mutableMapOf<String, SerializedProperty>()
                put(firstKeyPart, newMap)
                newMap
            }()
            map.addProperty(nextKeyParts, propertyValue)
        }
    }
}

private fun Vertex.setProperty(
        key: String,
        value: SerializedProperty?
) {
    when (value) {
        is Iterable<*> -> {
            val empty = value.foldIndexed(true) { idx, _, prop ->
                setProperty(key + nestedPropertyDelimiter + idx, prop)
                false
            }
            if (empty) {
                setProperty(key, emptyListToken)
            }
        }
        is Map<*, *> -> {
            @Suppress("UNCHECKED_CAST")
            value as Map<String, SerializedProperty?>
            value
                    .mapKeys { key + nestedPropertyDelimiter + it.key }
                    .forEach { k, v -> setProperty(k, v) }
        }
        null -> {}
        else -> property(key, value)
    }
}
