package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.Relationship

interface MultiBoundRelationship<FROM : Any, TO : Any> : MultiBoundPath<FROM, TO>, BoundRelationship<FROM, TO> {

    interface ToOne<FROM : Any, TO : Any> : MultiBoundRelationship<FROM, TO>, BoundPath.ToOne<FROM, TO>

    interface ToMany<FROM : Any, TO : Any> : MultiBoundRelationship<FROM, TO>, BoundPath.ToMany<FROM, TO> {
        override val path: Relationship.ToMany<FROM, TO>
    }

    interface ToSingle<FROM : Any, TO : Any> : ToOne<FROM, TO>, BoundPath.ToSingle<FROM, TO> {
        override val path: Relationship.ToSingle<FROM, TO>
    }

    interface ToOptional<FROM : Any, TO : Any> : ToOne<FROM, TO>, BoundPath.ToOptional<FROM, TO> {
        override val path: Relationship.ToOptional<FROM, TO>
    }
}
