package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

open class StepToMany<FROM, TO>(
        private val step: (StepTraverser<FROM>) -> GraphTraversal<*, TO>
) : Step.ToMany<FROM, TO> {

    override fun invoke(from: StepTraverser<FROM>) = step(from)
}
