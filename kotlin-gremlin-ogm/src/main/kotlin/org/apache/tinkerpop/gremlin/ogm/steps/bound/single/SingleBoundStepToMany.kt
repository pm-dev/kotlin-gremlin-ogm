package org.apache.tinkerpop.gremlin.ogm.steps.bound.single

import org.apache.tinkerpop.gremlin.ogm.steps.Step

/**
 * A [SingleBoundStep] that results to 0 or more 'TO' objects.
 */
internal data class SingleBoundStepToMany<FROM, TO>(
        override val from: FROM,
        override val step: Step.ToMany<FROM, TO>
) : SingleBoundStep.ToMany<FROM, TO>
