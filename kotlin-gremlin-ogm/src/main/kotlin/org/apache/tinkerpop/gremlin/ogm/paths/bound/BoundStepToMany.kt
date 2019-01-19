package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step

/**
 * A [BoundStep] that results to 0 or more objects for each [froms] object that
 * the traversed path starts with.
 */
internal data class BoundStepToMany<FROM : Vertex, TO>(
        override val froms: List<FROM>,
        override val step: Step.ToMany<FROM, TO>
) : BoundStep.ToMany<FROM, TO>
