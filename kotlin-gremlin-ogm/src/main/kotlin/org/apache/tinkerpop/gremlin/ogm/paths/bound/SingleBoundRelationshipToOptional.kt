package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundPath] that is bound to 1 'OUT' object and whose path is a [Relationship.ToOptional]
 */
class SingleBoundRelationshipToOptional<OUT : Any, IN : Any>(
        override val outV: OUT,
        override val path: Relationship.ToOptional<OUT, IN>
) : SingleBoundPath.ToOptional<OUT, IN>, BoundRelationship.ToOptional<OUT, IN>
