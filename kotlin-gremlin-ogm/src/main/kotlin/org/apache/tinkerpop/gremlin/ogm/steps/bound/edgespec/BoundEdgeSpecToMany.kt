package org.apache.tinkerpop.gremlin.ogm.steps.bound.edgespec

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec.EdgeSpec

/**
 * A [BoundEdgeSpec] that results to 0 or more objects for each [froms] object that
 * the traversed path starts with.
 */
internal data class BoundEdgeSpecToMany<FROM : Vertex, TO : Vertex>(
        override val froms: List<FROM>,
        override val step: EdgeSpec.ToMany<FROM, TO>
) : BoundEdgeSpec.ToMany<FROM, TO>
