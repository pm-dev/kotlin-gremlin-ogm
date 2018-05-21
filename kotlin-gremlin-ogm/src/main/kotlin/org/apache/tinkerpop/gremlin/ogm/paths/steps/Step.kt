package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A [Step] defines a single manipulation to the underlying GraphTraversal.
 */
interface Step<OUT, IN> : Path<OUT, IN> {

    override fun path() = listOf(this)

    open class ToSingle<OUT, IN>(
            private val step: (StepTraverser<OUT>) -> GraphTraversal<*, IN>
    ) : Path.ToSingle<OUT, IN>, Step<OUT, IN> {
        override fun invoke(from: StepTraverser<OUT>): GraphTraversal<*, IN> = step(from)
    }

    open class ToOptional<OUT, IN>(
            private val step: (StepTraverser<OUT>) -> GraphTraversal<*, IN>
    ) : Path.ToOptional<OUT, IN>, Step<OUT, IN> {
        override fun invoke(from: StepTraverser<OUT>): GraphTraversal<*, IN> = step(from)
    }

    open class ToMany<OUT, IN>(
            private val step: (StepTraverser<OUT>) -> GraphTraversal<*, IN>
    ) : Path.ToMany<OUT, IN>, Step<OUT, IN> {
        override fun invoke(from: StepTraverser<OUT>): GraphTraversal<*, IN> = step(from)
    }
}
