package org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.edgespec

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.Relationship

/**
 * Creates a [Relationship] that is uni-directional. When traversed from a 'FROM' object,
 * there will be exactly 1 'TO' object. When the [inverse] is traversed from a 'TO' object,
 * there will be exactly 1 'FROM' object.
 */
data class SingleToSingleAsymmetricEdgeSpec<FROM : Vertex, TO : Vertex>(
        override val name: String,
        override val direction: EdgeSpec.Direction = EdgeSpec.Direction.FORWARD
) : EdgeSpec.SingleToSingle.Asymmetric<FROM, TO>
