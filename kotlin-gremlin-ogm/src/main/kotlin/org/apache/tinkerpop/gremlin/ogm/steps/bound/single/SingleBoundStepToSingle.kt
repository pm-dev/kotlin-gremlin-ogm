package org.apache.tinkerpop.gremlin.ogm.steps.bound.single

import org.apache.tinkerpop.gremlin.ogm.steps.Step

/**
 * A [SingleBoundStep] that results to exactly 1 'TO' object.
 */
internal data class SingleBoundStepToSingle<FROM, TO>(
        override val from: FROM,
        override val step: Step.ToSingle<FROM, TO>
) : SingleBoundStep.ToSingle<FROM, TO>
