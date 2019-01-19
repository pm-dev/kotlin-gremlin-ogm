package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step
import org.apache.tinkerpop.gremlin.ogm.paths.steps.paths.Path

/**
 * A [SingleBoundStep] that results to 0 or more 'TO' objects.
 */
internal data class SingleBoundStepToMany<FROM : Vertex, TO>(
        override val from: FROM,
        override val step: Step.ToMany<FROM, TO>
) : SingleBoundStep.ToMany<FROM, TO>
