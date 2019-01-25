package org.apache.tinkerpop.gremlin.ogm.steps

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A step that doesn't advance to a new type but simply removes a subset of objects from the traversal based on
 * a predicate.
 */
internal data class Filter<TYPE>(val predicate: (TYPE) -> Boolean) : Step.ToOptional<TYPE, TYPE> {
    override fun invoke(from: StepTraverser<TYPE>): GraphTraversal<*, TYPE> =
            from.traversal.filter { traverser -> predicate(traverser.get()) }
}
