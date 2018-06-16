package org.apache.tinkerpop.gremlin.ogm.paths.relationships

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex


/**
 * The 'link' function can be used to Combine two [Connection]s into one.
 */

infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.SingleToOne<FROM, TO>.link(next: Connection.OptionalToOptional<TO, NEXT>): Connection.OptionalToOptional<FROM, NEXT> =
        Link.OptionalToOptional(first = this, last = next)
infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.OptionalToOne<FROM, TO>.link(next: Connection.OneToOptional<TO, NEXT>): Connection.OptionalToOptional<FROM, NEXT> =
        Link.OptionalToOptional(first = this, last = next)
infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.OptionalToOptional<FROM, TO>.link(next: Connection.OneToSingle<TO, NEXT>): Connection.OptionalToOptional<FROM, NEXT> =
        Link.OptionalToOptional(first = this, last = next)


infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.OptionalToSingle<FROM, TO>.link(next: Connection.OneToSingle<TO, NEXT>): Connection.OptionalToSingle<FROM, NEXT> =
        Link.OptionalToSingle(first = this, last = next)
infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.OneToSingle<FROM, TO>.link(next: Connection.OptionalToSingle<TO, NEXT>): Connection.OptionalToSingle<FROM, NEXT> =
        Link.OptionalToSingle(first = this, last = next)


infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.SingleToOptional<FROM, TO>.link(next: Connection.SingleToOne<TO, NEXT>): Connection.SingleToOptional<FROM, NEXT> =
        Link.SingleToOptional(first = this, last = next)
infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.SingleToSingle<FROM, TO>.link(next: Connection.SingleToOptional<TO, NEXT>): Connection.SingleToOptional<FROM, NEXT> =
        Link.SingleToOptional(first = this, last = next)


infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.FromOne<FROM, TO>.link(next: Connection.OptionalToMany<TO, NEXT>): Connection.OptionalToMany<FROM, NEXT> =
        Link.OptionalToMany(first = this, last = next)
infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.FromOptional<FROM, TO>.link(next: Connection.SingleToMany<TO, NEXT>): Connection.OptionalToMany<FROM, NEXT> =
        Link.OptionalToMany(first = this, last = next)
infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.OptionalToMany<FROM, TO>.link(next: Connection.FromOne<TO, NEXT>): Connection.OptionalToMany<FROM, NEXT> =
        Link.OptionalToMany(first = this, last = next)


infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.SingleToSingle<FROM, TO>.link(next: Connection.SingleToSingle<TO, NEXT>): Connection.SingleToSingle<FROM, NEXT> =
        Link.SingleToSingle(first = this, last = next)


infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.FromSingle<FROM, TO>.link(next: Connection.SingleToMany<TO, NEXT>): Connection.SingleToMany<FROM, NEXT> =
        Link.SingleToMany(first = this, last = next)
infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.SingleToMany<FROM, TO>.link(next: Connection.FromSingle<TO, NEXT>): Connection.SingleToMany<FROM, NEXT> =
        Link.SingleToMany(first = this, last = next)


infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.ManyToOptional<FROM, TO>.link(next: Connection.ToOne<TO, NEXT>): Connection.ManyToOptional<FROM, NEXT> =
        Link.ManyToOptional(first = this, last = next)
infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.ManyToSingle<FROM, TO>.link(next: Connection.ToOptional<TO, NEXT>): Connection.ManyToOptional<FROM, NEXT> =
        Link.ManyToOptional(first = this, last = next)
infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.ToOne<FROM, TO>.link(next: Connection.ManyToOptional<TO, NEXT>): Connection.ManyToOptional<FROM, NEXT> =
        Link.ManyToOptional(first = this, last = next)


infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.ManyToSingle<FROM, TO>.link(next: Connection.ToSingle<TO, NEXT>): Connection.ManyToSingle<FROM, NEXT> =
        Link.ManyToSingle(first = this, last = next)
infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.ToSingle<FROM, TO>.link(next: Connection.ManyToSingle<TO, NEXT>): Connection.ManyToSingle<FROM, NEXT> =
        Link.ManyToSingle(first = this, last = next)


infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.FromMany<FROM, TO>.link(next: Connection.ToMany<TO, NEXT>): Connection.ManyToMany<FROM, NEXT> =
        Link.ManyToMany(first = this, last = next)
infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection<FROM, TO>.link(next: Connection.ManyToMany<TO, NEXT>): Connection.ManyToMany<FROM, NEXT> =
        Link.ManyToMany(first = this, last = next)
infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.ManyToMany<FROM, TO>.link(next: Connection<TO, NEXT>): Connection.ManyToMany<FROM, NEXT> =
        Link.ManyToMany(first = this, last = next)
infix fun <FROM : Vertex, TO : Vertex, NEXT : Vertex> Connection.ManyToMany<FROM, TO>.link(next: Connection.ManyToMany<TO, NEXT>): Connection.ManyToMany<FROM, NEXT> =
        Link.ManyToMany(first = this, last = next)
