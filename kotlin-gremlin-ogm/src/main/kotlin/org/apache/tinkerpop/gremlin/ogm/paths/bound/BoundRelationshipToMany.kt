package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundRelationship] that results to 0 or more objects for each [from] object that
 * the traversed path starts with.
 */
class BoundRelationshipToMany<FROM : Vertex, TO : Vertex>(
        override val froms: List<FROM>,
        override val path: Relationship.ToMany<FROM, TO>
) : BoundRelationship.ToMany<FROM, TO>
