package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A step that maps the current object to zero or more new objects.
 */
class Map<OUT, IN>(private val map: (OUT) -> IN) : Step.ToSingle<OUT, IN>({
    it.traversal.map { map(it.get()) }
})

fun <OUT, IN, NEXT> Path.ToMany<OUT, IN>.map(map: (IN) -> NEXT): Path.ToMany<OUT, NEXT> = to(Map(map))
fun <OUT, IN, NEXT> Path.ToOptional<OUT, IN>.map(map: (IN) -> NEXT): Path.ToOptional<OUT, NEXT> = to(Map(map))
fun <OUT, IN, NEXT> Path.ToSingle<OUT, IN>.map(map: (IN) -> NEXT): Path.ToSingle<OUT, NEXT> = to(Map(map))
