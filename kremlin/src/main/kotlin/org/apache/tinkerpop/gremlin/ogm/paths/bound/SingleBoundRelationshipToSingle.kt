package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundPath] that is bound to 1 'FROM' object and whose path is a [Relationship.ToSingle]
 */
class SingleBoundRelationshipToSingle<FROM : Any, TO : Any>(
        override val from: FROM,
        override val path: Relationship.ToSingle<FROM, TO>
) : SingleBoundPath.ToSingle<FROM, TO>, BoundRelationship.ToSingle<FROM, TO>
