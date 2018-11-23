package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A step that maps the current object to a new object, or removes the current object if the map function returns null.
 */
class FilterMap<FROM, TO>(private val map: (FROM) -> TO?) : Step.ToOptional<FROM, TO>({ traverser ->
    traverser.traversal
            .map { map(it.get()) }
            .filter { it.get() != null }
            .map { it.get()!! }
})

fun <FROM, TO, NEXT> Path.ToMany<FROM, TO>.filterMap(map: (TO) -> NEXT?): Path.ToMany<FROM, NEXT> = to(FilterMap(map))
fun <FROM, TO, NEXT> Path.ToOptional<FROM, TO>.filterMap(map: (TO) -> NEXT?): Path.ToOptional<FROM, NEXT> = to(FilterMap(map))
fun <FROM, TO, NEXT> Path.ToSingle<FROM, TO>.filterMap(map: (TO) -> NEXT?): Path.ToOptional<FROM, NEXT> = to(FilterMap(map))

