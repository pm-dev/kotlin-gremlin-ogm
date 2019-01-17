package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundRelationship] that results to exactly 1 object for each to object that
 * the traversed path starts with.
 */
class BoundRelationshipToSingle<FROM : Vertex, TO : Vertex>(
        override val froms: List<FROM>,
        override val path: Relationship.ToSingle<FROM, TO>
) : BoundRelationship.ToSingle<FROM, TO>
