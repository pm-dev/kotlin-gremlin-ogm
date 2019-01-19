package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step

/**
 * A [SingleBoundStep] that results to 0 or 1 'TO' objects.
 */
internal data class SingleBoundStepToOptional<FROM : Vertex, TO>(
        override val from: FROM,
        override val step: Step.ToOptional<FROM, TO>
) : SingleBoundStep.ToOptional<FROM, TO>
