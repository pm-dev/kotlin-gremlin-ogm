package org.apache.tinkerpop.gremlin.ogm.steps.bound

import org.apache.tinkerpop.gremlin.ogm.steps.Step

/**
 * A [BoundStep] that results to 0 or 1 object for each [froms] object that
 * the traversed path starts with.
 */
internal data class BoundStepToOptional<FROM, TO>(
        override val froms: List<FROM>,
        override val step: Step.ToOptional<FROM, TO>
) : BoundStep.ToOptional<FROM, TO>
