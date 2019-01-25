package org.apache.tinkerpop.gremlin.ogm.steps.bound

import org.apache.tinkerpop.gremlin.ogm.steps.Step

/**
 * A [BoundStep] that results to 0 or more objects for each [froms] object that
 * the traversed path starts with.
 */
internal data class BoundStepToMany<FROM, TO>(
        override val froms: List<FROM>,
        override val step: Step.ToMany<FROM, TO>
) : BoundStep.ToMany<FROM, TO>
