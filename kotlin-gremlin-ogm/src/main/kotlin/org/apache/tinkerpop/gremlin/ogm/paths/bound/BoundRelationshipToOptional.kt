package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundRelationship] that results to 0 or 1 object for each [from] object that
 * the traversed path starts with.
 */
class BoundRelationshipToOptional<FROM : Vertex, TO : Vertex>(
        override val froms: Iterable<FROM>,
        override val path: Relationship.ToOptional<FROM, TO>
) : BoundRelationship.ToOptional<FROM, TO>
