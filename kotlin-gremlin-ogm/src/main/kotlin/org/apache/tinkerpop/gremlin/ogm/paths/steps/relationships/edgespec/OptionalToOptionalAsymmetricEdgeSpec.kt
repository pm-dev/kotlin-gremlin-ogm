package org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.edgespec

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.Relationship

/**
 * Creates a [Relationship] that is uni-directional. When traversed from a 'FROM' object,
 * there will be 0 or 1 'TO' objects. When the [inverse] is traversed from a 'TO' object,
 * there will be 0 or 1 'FROM' objects.
 */
data class OptionalToOptionalAsymmetricEdgeSpec<FROM : Vertex, TO : Vertex>(
        override val name: String,
        override val direction: EdgeSpec.Direction = EdgeSpec.Direction.FORWARD
) : EdgeSpec.OptionalToOptional.Asymmetric<FROM, TO>
