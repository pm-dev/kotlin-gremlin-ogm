package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundPath] that is bound to 1 'OUT' object and whose path is a [Relationship.ToMany]
 */
class SingleBoundRelationshipToMany<OUT : Any, IN : Any>(
        override val outV: OUT,
        override val path: Relationship.ToMany<OUT, IN>
) : SingleBoundPath.ToMany<OUT, IN>, BoundRelationship.ToMany<OUT, IN>
