package org.apache.tinkerpop.gremlin.ogm.relationships.bound

interface MultiBoundPath<FROM : Any, TO> : BoundPath<FROM, TO> {

    interface ToOne<FROM : Any, TO> : MultiBoundPath<FROM, TO>, BoundPath.ToOne<FROM, TO>

    interface ToMany<FROM : Any, TO> : MultiBoundPath<FROM, TO>, BoundPath.ToMany<FROM, TO>

    interface ToSingle<FROM : Any, TO> : ToOne<FROM, TO>, BoundPath.ToSingle<FROM, TO>

    interface ToOptional<FROM : Any, TO> : ToOne<FROM, TO>, BoundPath.ToOptional<FROM, TO>
}


