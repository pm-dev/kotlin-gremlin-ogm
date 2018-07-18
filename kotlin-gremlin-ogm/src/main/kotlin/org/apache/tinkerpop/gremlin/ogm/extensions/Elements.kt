package org.apache.tinkerpop.gremlin.ogm.extensions

import org.apache.tinkerpop.gremlin.ogm.exceptions.EmptyListTokenIsReserved
import org.apache.tinkerpop.gremlin.ogm.exceptions.EmptyMapTokenIsReserved
import org.apache.tinkerpop.gremlin.ogm.mappers.SerializedProperty
import org.apache.tinkerpop.gremlin.structure.Element


internal fun Element.getProperties(): Map<String, SerializedProperty> {
    val map = mutableMapOf<String, SerializedProperty>()
    properties<Any>().forEach { property ->
        map.addProperty(property.key(), property.value())
    }
    map.mapValuesInPlace { entry ->
        entry.value.listify()
    }
    return map
}

internal fun <T : Element> T.setProperties(
        newProperties: Map<String, SerializedProperty?>
): T {
    properties<Any>().forEach {
        it.remove()
    }
    newProperties.forEach { key, value ->
        when (value) {
            emptyListToken -> throw EmptyListTokenIsReserved(emptyListToken)
            emptyMapToken -> throw EmptyMapTokenIsReserved(emptyMapToken)
            else -> setProperty(key, value)
        }
    }
    return this
}

/**
 * This is internal and not private so we can check for invalid @Property.name values containing '.', which is invalid.
 */
internal const val nestedPropertyDelimiter = '.'

/**
 *
 * When we encounter an empty list, it's important to save a property value that represents an empty list,
 * rather than not saving anything, otherwise, when reading the value for that property later, it would appear that the
 * value is null, which is different from an empty list. This token is what we'll save to mark the property value
 * as an empty list. This value must never be changed after it's used to a graph.
 *
 * This is internal and not private for testing.
 */
internal const val emptyListToken = "474A56F1-6309-41B5-A632-AD53F57DBDAE"

/**
 *
 * When we encounter an empty map, it's important to save a property value that represents an empty map,
 * rather than not saving anything, otherwise, when reading the value for that property later, it would appear that the
 * value is null, which is different from an empty map. This token is what we'll save to mark the property value
 * as an empty map. This value must never be changed after it's used to a graph.
 *
 * This is internal and not private for testing.
 */
internal const val emptyMapToken = "9B94DCB9-D405-47C1-B56D-72F83C4E81D3"

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
                this.mapValuesInPlace { entry ->
                    entry.value.listify()
                }
                this.toList() ?: this
            }
            emptyMapToken -> emptyMap<Any, Any>()
            emptyListToken -> emptyList<Any>()
            else -> this
        }

/**
 * If every key to the map is an integer string, convert the map to a list, sorted by the integer value of the keys.
 * If any key is not a valid representation of an integer, return null.
 */
private fun Map<String, SerializedProperty>.toList(): List<SerializedProperty>? {
    data class IndexAndValue(val index: Int, val value: SerializedProperty)
    val indexToValue = mutableListOf<IndexAndValue>()
    for (entry in entries) {
        val index = entry.key.toIntOrNull() ?: return null
        indexToValue.add(IndexAndValue(index, entry.value))
    }
    indexToValue.sortBy {
        it.index
    }
    return indexToValue.map {
        it.value
    }
}


private fun MutableMap<String, SerializedProperty>.addProperty(propertyKey: String, propertyValue: SerializedScalarProperty) {
    val delimiterIndex = propertyKey.indexOf(nestedPropertyDelimiter)
    when (delimiterIndex) {
        -1 -> put(propertyKey, propertyValue)
        else -> {
            val firstKeyPart = propertyKey.substring(0, delimiterIndex)
            val nextKeyParts = propertyKey.substring(delimiterIndex + 1)
            @Suppress("UNCHECKED_CAST")
            val map = get(firstKeyPart) as MutableMap<String, SerializedProperty>? ?: kotlin.run {
                val newMap = mutableMapOf<String, SerializedProperty>()
                put(firstKeyPart, newMap)
                newMap
            }
            map.addProperty(nextKeyParts, propertyValue)
        }
    }
}

private fun Element.setProperty(
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
            if (value.isEmpty()) {
                setProperty(key, emptyMapToken)
            } else {
                value.mapKeys { entry ->
                    key + nestedPropertyDelimiter + entry.key
                }.forEach { k, v ->
                    setProperty(k, v)
                }
            }
        }
        null -> {}
        else -> property(key, value)
    }
}
