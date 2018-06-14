package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A step that maps the current object to zero or more new objects.
 */
class FlatMap<FROM, TO>(private val map: (FROM) -> Iterable<TO>) : Step.ToMany<FROM, TO>({
    it.traversal.flatMap { map(it.get()).iterator() }
})

fun <FROM, TO, NEXT> Path.ToMany<FROM, TO>.flatMap(map: (TO) -> Iterable<NEXT>): Path.ToMany<FROM, NEXT> = to(FlatMap(map))
fun <FROM, TO, NEXT> Path.ToOptional<FROM, TO>.flatMap(map: (TO) -> Iterable<NEXT>): Path.ToMany<FROM, NEXT> = to(FlatMap(map))
fun <FROM, TO, NEXT> Path.ToSingle<FROM, TO>.flatMap(map: (TO) -> Iterable<NEXT>): Path.ToMany<FROM, NEXT> = to(FlatMap(map))

