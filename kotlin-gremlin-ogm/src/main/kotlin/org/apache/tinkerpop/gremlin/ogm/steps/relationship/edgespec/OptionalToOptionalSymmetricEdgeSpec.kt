package org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship

/**
 * Creates a [Relationship] that is bi-directional. When traversed from a 'FROM' object,
 * there will be 0 or 1 'TO' objects. When the [inverse] is traversed from a 'TO' object,
 * there will be 0 or 1 'FROM' objects.
 */
data class OptionalToOptionalSymmetricEdgeSpec<TYPE : Vertex>(
        override val name: String
) : EdgeSpec.OptionalToOptional.Symmetric<TYPE>
