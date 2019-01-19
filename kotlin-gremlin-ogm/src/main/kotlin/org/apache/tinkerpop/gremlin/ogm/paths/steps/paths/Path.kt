package org.apache.tinkerpop.gremlin.ogm.paths.steps.paths

import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step
import org.apache.tinkerpop.gremlin.ogm.paths.steps.StepTraverser
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A path represents a [GraphTraversal] transformation of 2 [Step]s
 */
internal interface Path<FROM, MIDDLE, TO> : Step<FROM, TO> {

    /**
     * The first step.
     */
    val first: Step<FROM, MIDDLE>

    /**
     * The second step.
     */
    val last: Step<MIDDLE, TO>

    override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, TO> =
            last.invoke(StepTraverser(first(from), from.graphMapper))

    /**
     * A path that is either a [ToOptional] or [ToSingle]
     */
    interface ToOne<FROM, MIDDLE, TO> : Path<FROM, MIDDLE, TO>, Step.ToOne<FROM, TO> {

        override val first: Step.ToOne<FROM, MIDDLE>
        override val last: Step.ToOne<MIDDLE, TO>
    }

    /**
     * A path that does not change the number of objects that would result from the current g
     */
    interface ToSingle<FROM, MIDDLE, TO> : ToOne<FROM, MIDDLE, TO>, Step.ToSingle<FROM, TO> {

        override val first: Step.ToSingle<FROM, MIDDLE>
        override val last: Step.ToSingle<MIDDLE, TO>
    }

    /**
     * A path that may reduce the number of objects that would result from the current g
     */
    interface ToOptional<FROM, MIDDLE, TO> : ToOne<FROM, MIDDLE, TO>, Step.ToOptional<FROM, TO>

    /**
     * A path that may increase the number of objects that would result from the
     */
    interface ToMany<FROM, MIDDLE, TO> : Path<FROM, MIDDLE, TO>, Step.ToMany<FROM, TO>
}
