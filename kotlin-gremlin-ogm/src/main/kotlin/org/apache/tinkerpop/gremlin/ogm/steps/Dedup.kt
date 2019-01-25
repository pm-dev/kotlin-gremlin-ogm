package org.apache.tinkerpop.gremlin.ogm.steps

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A step that filters out duplicate objects at the current location to the g.
 */
internal class Dedup<TYPE> : Step.ToSingle<TYPE, TYPE> {

    override fun invoke(from: StepTraverser<TYPE>): GraphTraversal<*, TYPE> =
            from.traversal.dedup()

    override fun hashCode() = Dedup::class.hashCode()

    override fun equals(other: Any?) = other is Dedup<*>
}
