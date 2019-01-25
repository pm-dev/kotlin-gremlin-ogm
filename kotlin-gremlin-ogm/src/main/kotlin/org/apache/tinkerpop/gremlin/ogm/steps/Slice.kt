package org.apache.tinkerpop.gremlin.ogm.steps

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A path that filters out objects outside a given range.
 */
internal data class Slice<TYPE>(private val range: LongRange) : Step.ToOptional<TYPE, TYPE> {
    override fun invoke(from: StepTraverser<TYPE>): GraphTraversal<*, TYPE> =
            from.traversal.range(range.start, range.endInclusive + 1)
}

