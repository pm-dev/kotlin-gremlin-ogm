package org.apache.tinkerpop.gremlin.ogm.extensions

internal fun <K, V> MutableMap<K, V>.mapValuesInPlace(transform: (Map.Entry<K, V>) -> V) =
        entries.forEach { entry ->
            entry.setValue(transform(entry))
        }

internal fun <K, V> Sequence<Pair<K, V>>.toMultiMap(requireKeys: Iterable<K> = emptyList()): Map<K, List<V>> {
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

internal fun <K, V> Sequence<Pair<K, V>>.toOptionalMap(requireKeys: Iterable<K> = emptyList()): Map<K, V?> {
    val remainingRequiredKeys = requireKeys.toMutableSet()
    val map = associateTo(mutableMapOf<K, V?>()) {
        remainingRequiredKeys.remove(it.first)
        it
    }
    remainingRequiredKeys.forEach {
        map[it] = null
    }
    return map
}

internal fun <K, V> Sequence<Pair<K, V>>.toSingleMap(requireKeys: Iterable<K> = emptyList()): Map<K, V> {
    val remainingRequiredKeys = requireKeys.toMutableSet()
    val map = associateTo(mutableMapOf()) {
        remainingRequiredKeys.remove(it.first)
        it
    }
    if (remainingRequiredKeys.isNotEmpty()) {
        throw NoSuchElementException("Sequence is missing pair for keys $remainingRequiredKeys")
    }
    return map
}
