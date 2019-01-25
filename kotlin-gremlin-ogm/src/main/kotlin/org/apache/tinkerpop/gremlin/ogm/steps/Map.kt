package org.apache.tinkerpop.gremlin.ogm.steps

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A step that maps the current object to a new object.
 */
internal data class Map<FROM, TO>(private val map: (FROM) -> TO) : Step.ToSingle<FROM, TO> {
    override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, TO> =
            from.traversal.map { map(it.get()) }
}
