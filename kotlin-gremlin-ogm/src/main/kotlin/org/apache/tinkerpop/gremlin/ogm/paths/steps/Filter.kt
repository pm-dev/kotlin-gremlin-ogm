package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A step that doesn't advance to a new type but simply removes a subset of objects from the traversal based on
 * a predicate.
 */
class Filter<TYPE>(private val predicate: (TYPE) -> Boolean) : Step.ToOptional<TYPE, TYPE>({
    it.traversal.filter { predicate(it.get()) }
})

fun <OUT, IN> Path.ToMany<OUT, IN>.filter(predicate: (IN) -> Boolean): Path.ToMany<OUT, IN> = to(Filter(predicate))
fun <OUT, IN> Path.ToOptional<OUT, IN>.filter(predicate: (IN) -> Boolean): Path.ToOptional<OUT, IN> = to(Filter(predicate))
fun <OUT, IN> Path.ToSingle<OUT, IN>.filter(predicate: (IN) -> Boolean): Path.ToOptional<OUT, IN> = to(Filter(predicate))

