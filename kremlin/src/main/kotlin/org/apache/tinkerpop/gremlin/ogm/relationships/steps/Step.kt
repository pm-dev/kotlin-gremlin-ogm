package org.apache.tinkerpop.gremlin.ogm.relationships.steps

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A [Step] defines a single manipulation to the underlying GraphTraversal.
 */
interface Step<FROM, TO> : Path<FROM, TO> {

    override fun path() = listOf(this)

    open class ToSingle<FROM, TO>(
            private val step: (StepTraverser<FROM>) -> GraphTraversal<*, TO>
    ) : Path.ToSingle<FROM, TO>, Step<FROM, TO> {
        override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, TO> = step(from)
    }

    open class ToOptional<FROM, TO>(
            private val step: (StepTraverser<FROM>) -> GraphTraversal<*, TO>
    ) : Path.ToOptional<FROM, TO>, Step<FROM, TO> {
        override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, TO> = step(from)
    }

    open class ToMany<FROM, TO>(
            private val step: (StepTraverser<FROM>) -> GraphTraversal<*, TO>
    ) : Path.ToMany<FROM, TO>, Step<FROM, TO> {
        override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, TO> = step(from)
    }
}
