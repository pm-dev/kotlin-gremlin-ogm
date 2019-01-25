package org.apache.tinkerpop.gremlin.ogm.steps.bound

import org.apache.tinkerpop.gremlin.ogm.steps.Step
import org.apache.tinkerpop.gremlin.ogm.steps.StepTraverser
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal


/**
 * A [BoundStep] composes a [Step] with multiple [FROM] objects to start the step's traversal with
 */
interface BoundStep<FROM, TO> {

    /**
     * The objects to start traversing with.
     */
    val froms: List<FROM>

    /**
     * The step that performs the traversal
     */
    val step: Step<FROM, TO>

    /**
     * A [BoundStep] that results to a optional or non-optional (aka single) object
     * for each [froms] object that the path is traversed with.
     */
    interface ToOne<FROM, TO> : BoundStep<FROM, TO> {

        override val step: Step.ToOne<FROM, TO>
    }

    /**
     * A [BoundStep] that results to 0 or more objects for each [froms] object that
     * the traversed path starts with.
     */
    interface ToMany<FROM, TO> : BoundStep<FROM, TO> {

        override val step: Step.ToMany<FROM, TO>
    }

    /**
     * A [BoundStep] that results to a non-optional object
     * for each [froms] object that the path is traversed with.
     */
    interface ToSingle<FROM, TO> : ToOne<FROM, TO> {

        override val step: Step.ToSingle<FROM, TO>
    }

    /**
     * A [BoundStep] that results to an optional object
     * for each [froms] object that the path is traversed with.
     */
    interface ToOptional<FROM, TO> : ToOne<FROM, TO> {

        override val step: Step.ToOptional<FROM, TO>
    }
}
