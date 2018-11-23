package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A step that doesn't advance to a new type but simply removes a subset of objects from the traversal based on
 * a predicate.
 */
class Filter<TYPE>(private val predicate: (TYPE) -> Boolean) : Step.ToOptional<TYPE, TYPE>({
    it.traversal.filter { traverser -> predicate(traverser.get()) }
})

fun <FROM, TO> Path.ToMany<FROM, TO>.filter(predicate: (TO) -> Boolean): Path.ToMany<FROM, TO> = to(Filter(predicate))
fun <FROM, TO> Path.ToOptional<FROM, TO>.filter(predicate: (TO) -> Boolean): Path.ToOptional<FROM, TO> = to(Filter(predicate))
fun <FROM, TO> Path.ToSingle<FROM, TO>.filter(predicate: (TO) -> Boolean): Path.ToOptional<FROM, TO> = to(Filter(predicate))

