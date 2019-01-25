package org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship

/**
 * Creates a [Relationship] that is bi-directional. When traversed from a 'FROM' object,
 * there will be exactly 1 'TO' object. When the [inverse] is traversed from a 'TO' object,
 * there will be exactly 1 'FROM' object.
 */
data class SingleToSingleSymmetricEdgeSpec<TYPE : Vertex>(
        override val name: String
) : EdgeSpec.SingleToSingle.Symmetric<TYPE>
