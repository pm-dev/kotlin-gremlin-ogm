package org.apache.tinkerpop.gremlin.ogm.relationships.bound


interface SingleBoundRelationship<FROM : Any, TO : Any> : SingleBoundPath<FROM, TO>, BoundRelationship<FROM, TO> {

    interface ToOne<FROM : Any, TO : Any> : SingleBoundPath.ToOne<FROM, TO>, BoundRelationship.ToOne<FROM, TO>

    interface ToMany<FROM : Any, TO : Any> : SingleBoundPath.ToMany<FROM, TO>, BoundRelationship.ToMany<FROM, TO>

    interface ToSingle<FROM : Any, TO : Any> : ToOne<FROM, TO>, SingleBoundPath.ToSingle<FROM, TO>, BoundRelationship.ToSingle<FROM, TO>

    interface ToOptional<FROM : Any, TO : Any> : ToOne<FROM, TO>, SingleBoundPath.ToOptional<FROM, TO>, BoundRelationship.ToOptional<FROM, TO>
}
