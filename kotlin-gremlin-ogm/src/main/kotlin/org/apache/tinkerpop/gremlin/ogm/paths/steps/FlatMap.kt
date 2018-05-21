package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A step that maps the current object to zero or more new objects.
 */
class FlatMap<OUT, IN>(private val map: (OUT) -> Iterable<IN>) : Step.ToMany<OUT, IN>({
    it.traversal.flatMap { map(it.get()).iterator() }
})

fun <OUT, IN, NEXT> Path.ToMany<OUT, IN>.flatMap(map: (IN) -> Iterable<NEXT>): Path.ToMany<OUT, NEXT> = to(FlatMap(map))
fun <OUT, IN, NEXT> Path.ToOptional<OUT, IN>.flatMap(map: (IN) -> Iterable<NEXT>): Path.ToMany<OUT, NEXT> = to(FlatMap(map))
fun <OUT, IN, NEXT> Path.ToSingle<OUT, IN>.flatMap(map: (IN) -> Iterable<NEXT>): Path.ToMany<OUT, NEXT> = to(FlatMap(map))

