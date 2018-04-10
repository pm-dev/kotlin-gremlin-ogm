package org.apache.tinkerpop.gremlin.ogm.relationships.bound

interface SingleBoundPath<FROM : Any, TO> : BoundPath<FROM, TO> {

    val from: FROM

    override val froms: Iterable<FROM> get() = listOf(from)

    interface ToOne<FROM : Any, TO> : SingleBoundPath<FROM, TO>, BoundPath.ToOne<FROM, TO>

    interface ToMany<FROM : Any, TO> : SingleBoundPath<FROM, TO>, BoundPath.ToMany<FROM, TO>

    interface ToSingle<FROM : Any, TO> : ToOne<FROM, TO>, BoundPath.ToSingle<FROM, TO>

    interface ToOptional<FROM : Any, TO> : ToOne<FROM, TO>, BoundPath.ToOptional<FROM, TO>
}
