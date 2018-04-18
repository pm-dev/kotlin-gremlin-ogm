package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundRelationship] that results in exactly 1 object for each [from] object that
 * the traversed path starts with.
 */
class BoundRelationshipToSingle<FROM : Any, TO : Any>(
        override val froms: Iterable<FROM>,
        override val path: Relationship.ToSingle<FROM, TO>
) : BoundRelationship.ToSingle<FROM, TO>
