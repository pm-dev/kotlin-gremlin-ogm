package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.Relationship
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.edgespec.EdgeSpec

/**
 * A [BoundEdgeSpec] that results to 0 or 1 object for each [froms] object that
 * the traversed path starts with.
 */
internal data class BoundEdgeSpecToOptional<FROM : Vertex, TO : Vertex>(
        override val froms: List<FROM>,
        override val step: EdgeSpec.ToOptional<FROM, TO>
) : BoundEdgeSpec.ToOptional<FROM, TO>
