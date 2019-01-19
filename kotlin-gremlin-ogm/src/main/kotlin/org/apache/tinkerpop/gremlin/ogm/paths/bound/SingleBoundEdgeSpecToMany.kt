package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.Relationship
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.edgespec.EdgeSpec

/**
 * A [SingleBoundEdgeSpec] whose spec is a [Relationship.ToMany]
 */
internal data class SingleBoundEdgeSpecToMany<FROM : Vertex, TO : Vertex>(
        override val from: FROM,
        override val step: EdgeSpec.ToMany<FROM, TO>
) : SingleBoundEdgeSpec.ToMany<FROM, TO>
