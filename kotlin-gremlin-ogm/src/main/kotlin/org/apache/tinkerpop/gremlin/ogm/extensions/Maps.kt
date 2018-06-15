package org.apache.tinkerpop.gremlin.ogm.extensions

internal fun <K, V> Map<K?, V?>.filterNulls(): Map<K, V> =
        entries
                .filter { it.key != null && it.value != null }
                .map { it.key!! to it.value!! }
                .associate { it }

internal fun <K, V> Map<K, V?>.filterNullValues(): Map<K, V> =
        filter { it.value != null }
                .mapValues { it.value!! }


