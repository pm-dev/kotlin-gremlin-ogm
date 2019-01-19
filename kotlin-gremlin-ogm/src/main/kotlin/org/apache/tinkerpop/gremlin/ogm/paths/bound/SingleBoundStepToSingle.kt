package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step
import org.apache.tinkerpop.gremlin.ogm.paths.steps.paths.Path

/**
 * A [SingleBoundStep] that results to exactly 1 'TO' object.
 */
internal data class SingleBoundStepToSingle<FROM : Vertex, TO>(
        override val from: FROM,
        override val step: Step.ToSingle<FROM, TO>
) : SingleBoundStep.ToSingle<FROM, TO>
