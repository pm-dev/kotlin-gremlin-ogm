package org.apache.tinkerpop.gremlin.ogm.extensions

internal fun <K, V> MutableMap<K, V>.mapValuesInPlace(transform: (Map.Entry<K, V>) -> V) =
    entries.forEach { it.setValue(transform(it)) }

internal fun <K, V, T> Iterator<T>.toMultiMap(requireKeys: Iterable<K> = emptyList(), transform: (T) -> Pair<K, V>): Map<K, List<V>> {
    val remainingRequiredKeys = requireKeys.toMutableSet()
    val map = mutableMapOf<K, MutableList<V>>()
    forEach {
        val pair = transform(it)
        remainingRequiredKeys.remove(pair.first)
        map[pair.first]?.add(pair.second) ?: {
            map[pair.first] = mutableListOf(pair.second)
        }()
    }
    remainingRequiredKeys.forEach {
        map[it] = mutableListOf()
    }
    return map
}
