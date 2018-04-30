package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundRelationship] that results in 0 or 1 object for each [from] object that
 * the traversed path starts with.
 */
class BoundRelationshipToOptional<OUT : Any, IN : Any>(
        override val outVs: Iterable<OUT>,
        override val path: Relationship.ToOptional<OUT, IN>
) : BoundRelationship.ToOptional<OUT, IN>
