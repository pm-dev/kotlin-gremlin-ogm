package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

open class StepToOptional<FROM, TO>(
        private val step: (StepTraverser<FROM>) -> GraphTraversal<*, TO>
) : Step.ToOptional<FROM, TO>, Step<FROM, TO> {

    override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, TO> = step(from)
}
