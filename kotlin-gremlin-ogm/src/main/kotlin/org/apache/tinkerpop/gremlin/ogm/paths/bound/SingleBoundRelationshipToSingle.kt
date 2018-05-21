package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundPath] that is bound to 1 'OUT' object and whose path is a [Relationship.ToSingle]
 */
class SingleBoundRelationshipToSingle<OUT : Any, IN : Any>(
        override val outV: OUT,
        override val path: Relationship.ToSingle<OUT, IN>
) : SingleBoundPath.ToSingle<OUT, IN>, BoundRelationship.ToSingle<OUT, IN>
