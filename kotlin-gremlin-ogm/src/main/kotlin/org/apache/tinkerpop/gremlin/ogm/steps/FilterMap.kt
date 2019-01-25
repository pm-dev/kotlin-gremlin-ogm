package org.apache.tinkerpop.gremlin.ogm.steps

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A step that maps the current object to a new object, or removes the current object if the map function returns null.
 */
internal data class FilterMap<FROM, TO>(private val map: (FROM) -> TO?) : Step.ToOptional<FROM, TO> {
    override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, TO> =
            from.traversal
                    .map { map(it.get()) }
                    .filter { it.get() != null }
                    .map { it.get()!! }
}
