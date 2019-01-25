package org.apache.tinkerpop.gremlin.ogm.steps.bound

import org.apache.tinkerpop.gremlin.ogm.steps.Step

/**
 * A [BoundStep] that results to exactly 1 object for each [froms] object that
 * the traversed path starts with.
 */
internal data class BoundStepToSingle<FROM, TO>(
        override val froms: List<FROM>,
        override val step: Step.ToSingle<FROM, TO>
) : BoundStep.ToSingle<FROM, TO>
