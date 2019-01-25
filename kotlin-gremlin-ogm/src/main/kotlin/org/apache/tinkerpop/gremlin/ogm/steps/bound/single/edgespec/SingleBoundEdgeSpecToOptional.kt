package org.apache.tinkerpop.gremlin.ogm.steps.bound.single.edgespec

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec.EdgeSpec

/**
 * A [SingleBoundEdgeSpec] whose spec is a [Relationship.ToOptional]
 */
internal data class SingleBoundEdgeSpecToOptional<FROM : Vertex, TO : Vertex>(
        override val from: FROM,
        override val step: EdgeSpec.ToOptional<FROM, TO>
) : SingleBoundEdgeSpec.ToOptional<FROM, TO>
