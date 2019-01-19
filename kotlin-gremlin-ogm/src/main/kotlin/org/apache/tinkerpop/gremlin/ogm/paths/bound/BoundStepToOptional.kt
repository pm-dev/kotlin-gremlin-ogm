package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step

/**
 * A [BoundStep] that results to 0 or 1 object for each [froms] object that
 * the traversed path starts with.
 */
internal data class BoundStepToOptional<FROM : Vertex, TO>(
        override val froms: List<FROM>,
        override val step: Step.ToOptional<FROM, TO>
) : BoundStep.ToOptional<FROM, TO>
