package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A step that maps the current object to zero or more new objects.
 */
class Map<FROM, TO>(private val map: (FROM) -> TO) : Step.ToSingle<FROM, TO>({
    it.traversal.map { map(it.get()) }
})

fun <FROM, TO, NEXT> Path.ToMany<FROM, TO>.map(map: (TO) -> NEXT): Path.ToMany<FROM, NEXT> = to(Map(map))
fun <FROM, TO, NEXT> Path.ToOptional<FROM, TO>.map(map: (TO) -> NEXT): Path.ToOptional<FROM, NEXT> = to(Map(map))
fun <FROM, TO, NEXT> Path.ToSingle<FROM, TO>.map(map: (TO) -> NEXT): Path.ToSingle<FROM, NEXT> = to(Map(map))
