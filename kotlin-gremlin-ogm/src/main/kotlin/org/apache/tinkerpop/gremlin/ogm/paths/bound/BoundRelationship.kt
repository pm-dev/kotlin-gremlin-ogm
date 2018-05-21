package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundRelationship] encapsulates a Relationship, as well as the object(s) the relationship starts from
 */
interface BoundRelationship<OUT : Any, IN : Any> : BoundPath<OUT, IN> {

    override val path: Relationship<OUT, IN>

    /**
     * A [BoundRelationship] where the relationship is a [Relationship.ToOne]
     */
    interface ToOne<OUT : Any, IN : Any> : BoundRelationship<OUT, IN>, BoundPath.ToOne<OUT, IN> {

        override val path: Relationship.ToOne<OUT, IN>
    }

    /**
     * A [BoundRelationship] where the relationship is a [Relationship.ToMany]
     */
    interface ToMany<OUT : Any, IN : Any> : BoundRelationship<OUT, IN>, BoundPath.ToMany<OUT, IN> {

        override val path: Relationship.ToMany<OUT, IN>
    }

    /**
     * A [BoundRelationship] where the relationship is a [Relationship.ToSingle]
     */
    interface ToSingle<OUT : Any, IN : Any> : ToOne<OUT, IN>, BoundPath.ToSingle<OUT, IN> {

        override val path: Relationship.ToSingle<OUT, IN>
    }

    /**
     * A [BoundRelationship] where the relationship is a [Relationship.ToOptional]
     */
    interface ToOptional<OUT : Any, IN : Any> : ToOne<OUT, IN>, BoundPath.ToOptional<OUT, IN> {

        override val path: Relationship.ToOptional<OUT, IN>
    }
}
