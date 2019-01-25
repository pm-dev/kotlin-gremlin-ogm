package org.apache.tinkerpop.gremlin.ogm.steps

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A path that sorts the traversal's objects by a given comparators. The 2nd comparator is the secondary ordering,
 * 3rd comparator is the tertiary ordering, etc.
 */
internal data class Sort<TYPE>(private val comparators: Collection<Comparator<TYPE>>) : Step.ToSingle<TYPE, TYPE> {

    constructor(comparator: Comparator<TYPE>) : this(listOf(comparator))

    override fun invoke(from: StepTraverser<TYPE>): GraphTraversal<*, TYPE> =
            comparators.fold(initial = from.traversal.order(), operation = GraphTraversal<out Any, TYPE>::by)

}

