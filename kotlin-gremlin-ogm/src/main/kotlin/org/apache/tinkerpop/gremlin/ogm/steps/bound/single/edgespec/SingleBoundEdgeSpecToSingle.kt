package org.apache.tinkerpop.gremlin.ogm.steps.bound.single.edgespec

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec.EdgeSpec

/**
 * A [SingleBoundEdgeSpec] whose spec is a [Relationship.ToSingle]
 */
internal data class SingleBoundEdgeSpecToSingle<FROM : Vertex, TO : Vertex>(
        override val from: FROM,
        override val step: EdgeSpec.ToSingle<FROM, TO>
) : SingleBoundEdgeSpec.ToSingle<FROM, TO>
