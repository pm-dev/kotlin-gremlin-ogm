package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundRelationship] that results in exactly 1 object for each inV object that
 * the traversed path starts with.
 */
class BoundRelationshipToSingle<OUT : Any, IN : Any>(
        override val outVs: Iterable<OUT>,
        override val path: Relationship.ToSingle<OUT, IN>
) : BoundRelationship.ToSingle<OUT, IN>
