package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.Relationship

/**
 * A [BoundRelationship] encapsulates a Relationship, as well as the object(s) the relationship starts from
 */
interface BoundRelationship<FROM : Any, TO : Any> : BoundPath<FROM, TO> {

    override val path: Relationship<FROM, TO>

    interface ToOne<FROM : Any, TO : Any> : BoundRelationship<FROM, TO>, BoundPath.ToOne<FROM, TO>

    interface ToMany<FROM : Any, TO : Any> : BoundRelationship<FROM, TO>, BoundPath.ToMany<FROM, TO> {

        override val path: Relationship.ToMany<FROM, TO>

        override fun add(from: FROM): MultiBoundRelationship.ToMany<FROM, TO>
        override fun add(vararg from: FROM): MultiBoundRelationship.ToMany<FROM, TO>
        override fun add(from: Iterable<FROM>): MultiBoundRelationship.ToMany<FROM, TO>
    }

    interface ToSingle<FROM : Any, TO : Any> : ToOne<FROM, TO>, BoundPath.ToSingle<FROM, TO> {

        override val path: Relationship.ToSingle<FROM, TO>

        override fun add(from: FROM): MultiBoundRelationship.ToSingle<FROM, TO>
        override fun add(vararg from: FROM): MultiBoundRelationship.ToSingle<FROM, TO>
        override fun add(from: Iterable<FROM>): MultiBoundRelationship.ToSingle<FROM, TO>
    }

    interface ToOptional<FROM : Any, TO : Any> : ToOne<FROM, TO>, BoundPath.ToOptional<FROM, TO> {

        override val path: Relationship.ToOptional<FROM, TO>

        override fun add(from: FROM): MultiBoundRelationship.ToOptional<FROM, TO>
        override fun add(vararg from: FROM): MultiBoundRelationship.ToOptional<FROM, TO>
        override fun add(from: Iterable<FROM>): MultiBoundRelationship.ToOptional<FROM, TO>
    }
}
