package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundPath] that is bound to 1 'FROM' object and whose path is a [Relationship.ToMany]
 */
class SingleBoundRelationshipToMany<FROM : Any, TO : Any>(
        override val from: FROM,
        override val path: Relationship.ToMany<FROM, TO>
) : SingleBoundPath.ToMany<FROM, TO>, BoundRelationship.ToMany<FROM, TO>
