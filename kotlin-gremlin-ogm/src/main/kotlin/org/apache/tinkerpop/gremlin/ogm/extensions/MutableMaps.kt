package org.apache.tinkerpop.gremlin.ogm.extensions

internal fun <K, V> MutableMap<K, V>.mapValuesInPlace(transform: (Map.Entry<K, V>) -> V) =
        entries.forEach { entry ->
            entry.setValue(transform(entry))
        }

internal fun <K, V> Iterator<Pair<K, V>>.toMultiMap(requireKeys: Collection<K> = emptyList()): Map<K, List<V>> {
    val remainingRequiredKeys = requireKeys.toMutableSet()
    val map = mutableMapOf<K, MutableList<V>>()
    forEach {
        remainingRequiredKeys.remove(it.first)
        map[it.first]?.add(it.second) ?: kotlin.run {
            map[it.first] = mutableListOf(it.second)
        }
    }
    remainingRequiredKeys.forEach {
        map[it] = mutableListOf()
    }
    return map
}


internal fun <K, V> Iterator<Pair<K, V>>.toOptionalMap(requireKeys: Collection<K> = emptyList()): Map<K, V?> {
    val remainingRequiredKeys = requireKeys.toMutableSet()
    val finalMap = mutableMapOf<K, V?>()
    forEach { pair ->
        if (finalMap[pair.first] != null) {
            throw IllegalArgumentException("Two pairs have the same key which shouldn't be possible in an optional map")
        }
        remainingRequiredKeys.remove(pair.first)
        finalMap[pair.first] = pair.second
    }
    remainingRequiredKeys.forEach {
        finalMap[it] = null
    }
    return finalMap
}

internal fun <K, V> Iterator<Pair<K, V>>.toSingleMap(requireKeys: Collection<K> = emptyList()): Map<K, V> {
    val remainingRequiredKeys = requireKeys.toMutableSet()
    val finalMap = mutableMapOf<K, V>()
    forEach { pair ->
        if (finalMap[pair.first] != null) {
            throw IllegalArgumentException("Two pairs have the same key which shouldn't be possible in a single map")
        }
        remainingRequiredKeys.remove(pair.first)
        finalMap[pair.first] = pair.second
    }
    if (remainingRequiredKeys.isNotEmpty()) {
        throw NoSuchElementException("Sequence is missing pair for keys $remainingRequiredKeys")
    }
    return finalMap
}
