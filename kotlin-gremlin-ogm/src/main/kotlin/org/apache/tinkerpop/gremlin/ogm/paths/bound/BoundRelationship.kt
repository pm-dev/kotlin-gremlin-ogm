package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundRelationship] encapsulates a Relationship, as well as the object(s) the relationship starts from
 */
interface BoundRelationship<FROM : Any, TO : Any> : BoundPath<FROM, TO> {

    override val path: Relationship<FROM, TO>

    /**
     * A [BoundRelationship] where the relationship is a [Relationship.ToOne]
     */
    interface ToOne<FROM : Any, TO : Any> : BoundRelationship<FROM, TO>, BoundPath.ToOne<FROM, TO> {

        override val path: Relationship.ToOne<FROM, TO>
    }

    /**
     * A [BoundRelationship] where the relationship is a [Relationship.ToMany]
     */
    interface ToMany<FROM : Any, TO : Any> : BoundRelationship<FROM, TO>, BoundPath.ToMany<FROM, TO> {

        override val path: Relationship.ToMany<FROM, TO>
    }

    /**
     * A [BoundRelationship] where the relationship is a [Relationship.ToSingle]
     */
    interface ToSingle<FROM : Any, TO : Any> : ToOne<FROM, TO>, BoundPath.ToSingle<FROM, TO> {

        override val path: Relationship.ToSingle<FROM, TO>
    }

    /**
     * A [BoundRelationship] where the relationship is a [Relationship.ToOptional]
     */
    interface ToOptional<FROM : Any, TO : Any> : ToOne<FROM, TO>, BoundPath.ToOptional<FROM, TO> {

        override val path: Relationship.ToOptional<FROM, TO>
    }
}
