package org.apache.tinkerpop.gremlin.ogm.steps

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A step that maps the current object to zero or more new objects.
 */
internal data class FlatMap<FROM, TO>(private val map: (FROM) -> Iterable<TO>) : Step.ToMany<FROM, TO> {
    override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, TO> =
            from.traversal.flatMap { map(it.get()).iterator() }
}
