package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A path that sorts the traversal's objects by a given comparator.
 */
class SortStep<TYPE>(private val comparator: Comparator<TYPE>) : Step.ToSingle<TYPE, TYPE>({
    it.traversal.order().by(comparator)
})

fun <OUT, IN> Path.ToMany<OUT, IN>.sort(comparator: Comparator<IN>): Path.ToMany<OUT, IN> = to(SortStep(comparator))
