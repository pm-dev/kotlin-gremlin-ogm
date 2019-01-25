package org.apache.tinkerpop.gremlin.ogm.steps.bound.single

import org.apache.tinkerpop.gremlin.ogm.steps.Step

/**
 * A [SingleBoundStep] that results to 0 or 1 'TO' objects.
 */
internal data class SingleBoundStepToOptional<FROM, TO>(
        override val from: FROM,
        override val step: Step.ToOptional<FROM, TO>
) : SingleBoundStep.ToOptional<FROM, TO>
