package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step


/**
 * A [BoundStep] composes a [Step] with multiple [FROM] objects to start the step's traversal with
 */
interface BoundStep<FROM : Vertex, TO> {

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
    interface ToOne<FROM : Vertex, TO> : BoundStep<FROM, TO> {

        override val step: Step.ToOne<FROM, TO>
    }

    /**
     * A [BoundStep] that results to 0 or more objects for each [froms] object that
     * the traversed path starts with.
     */
    interface ToMany<FROM : Vertex, TO> : BoundStep<FROM, TO> {

        override val step: Step.ToMany<FROM, TO>
    }

    /**
     * A [BoundStep] that results to a non-optional object
     * for each [froms] object that the path is traversed with.
     */
    interface ToSingle<FROM : Vertex, TO> : ToOne<FROM, TO> {

        override val step: Step.ToSingle<FROM, TO>
    }

    /**
     * A [BoundStep] that results to an optional object
     * for each [froms] object that the path is traversed with.
     */
    interface ToOptional<FROM : Vertex, TO> : ToOne<FROM, TO> {

        override val step: Step.ToOptional<FROM, TO>
    }
}
