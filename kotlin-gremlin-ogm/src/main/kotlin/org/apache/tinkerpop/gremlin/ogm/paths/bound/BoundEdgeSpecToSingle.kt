package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.Relationship
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.edgespec.EdgeSpec

/**
 * A [BoundEdgeSpec] that results to exactly 1 object for each to object that
 * the traversed path starts with.
 */
internal data class BoundEdgeSpecToSingle<FROM : Vertex, TO : Vertex>(
        override val froms: List<FROM>,
        override val step: EdgeSpec.ToSingle<FROM, TO>
) : BoundEdgeSpec.ToSingle<FROM, TO>
