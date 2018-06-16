package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundPath] that is bound to 1 'FROM' object and whose path is a [Relationship.ToOptional]
 */
class SingleBoundRelationshipToOptional<FROM : Vertex, TO : Vertex>(
        override val from: FROM,
        override val path: Relationship.ToOptional<FROM, TO>
) : SingleBoundPath.ToOptional<FROM, TO>, BoundRelationship.ToOptional<FROM, TO>
