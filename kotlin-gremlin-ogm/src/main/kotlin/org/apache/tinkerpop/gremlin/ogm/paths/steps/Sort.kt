package org.apache.tinkerpop.gremlin.ogm.paths.steps

/**
 * A path that sorts the g's objects by a given comparator.
 */
internal class Sort<TYPE>(comparator: Comparator<TYPE>) : StepToSingle<TYPE, TYPE>({
    it.traversal.order().by(comparator)
})

