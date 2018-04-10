package org.apache.tinkerpop.gremlin.ogm.relationships.steps

/**
 * A path that sorts the traversal's objects by a given comparator.
 */
class SortStep<TYPE>(private val comparator: Comparator<TYPE>) : Step.ToSingle<TYPE, TYPE>({
    it.traversal.order().by(comparator)
})

fun <FROM, TO> Path.ToMany<FROM, TO>.sort(comparator: Comparator<TO>): Path.ToMany<FROM, TO> = to(SortStep(comparator))
