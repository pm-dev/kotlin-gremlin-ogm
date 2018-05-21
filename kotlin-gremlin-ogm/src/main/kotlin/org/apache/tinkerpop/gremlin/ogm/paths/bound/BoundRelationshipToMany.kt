package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundRelationship] that results in 0 or more objects for each [from] object that
 * the traversed path starts with.
 */
class BoundRelationshipToMany<OUT : Any, IN : Any>(
        override val outVs: Iterable<OUT>,
        override val path: Relationship.ToMany<OUT, IN>
) : BoundRelationship.ToMany<OUT, IN>
