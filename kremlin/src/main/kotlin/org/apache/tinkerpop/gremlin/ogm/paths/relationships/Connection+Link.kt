package org.apache.tinkerpop.gremlin.ogm.paths.relationships


/**
 * The 'link' function can be used to Combine two [Connection]s into one.
 */

infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.SingleToOne<FROM, TO>.link(next: Connection.OptionalToOptional<TO, NEXT>): Connection.OptionalToOptional<FROM, NEXT> =
        Link.OptionalToOptional(first = this, last = next)
infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.OptionalToOne<FROM, TO>.link(next: Connection.OneToOptional<TO, NEXT>): Connection.OptionalToOptional<FROM, NEXT> =
        Link.OptionalToOptional(first = this, last = next)
infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.OptionalToOptional<FROM, TO>.link(next: Connection.OneToSingle<TO, NEXT>): Connection.OptionalToOptional<FROM, NEXT> =
        Link.OptionalToOptional(first = this, last = next)


infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.OptionalToSingle<FROM, TO>.link(next: Connection.OneToSingle<TO, NEXT>): Connection.OptionalToSingle<FROM, NEXT> =
        Link.OptionalToSingle(first = this, last = next)
infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.OneToSingle<FROM, TO>.link(next: Connection.OptionalToSingle<TO, NEXT>): Connection.OptionalToSingle<FROM, NEXT> =
        Link.OptionalToSingle(first = this, last = next)


infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.SingleToOptional<FROM, TO>.link(next: Connection.SingleToOne<TO, NEXT>): Connection.SingleToOptional<FROM, NEXT> =
        Link.SingleToOptional(first = this, last = next)
infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.SingleToSingle<FROM, TO>.link(next: Connection.SingleToOptional<TO, NEXT>): Connection.SingleToOptional<FROM, NEXT> =
        Link.SingleToOptional(first = this, last = next)


infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.FromOne<FROM, TO>.link(next: Connection.OptionalToMany<TO, NEXT>): Connection.OptionalToMany<FROM, NEXT> =
        Link.OptionalToMany(first = this, last = next)
infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.FromOptional<FROM, TO>.link(next: Connection.SingleToMany<TO, NEXT>): Connection.OptionalToMany<FROM, NEXT> =
        Link.OptionalToMany(first = this, last = next)
infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.OptionalToMany<FROM, TO>.link(next: Connection.FromOne<TO, NEXT>): Connection.OptionalToMany<FROM, NEXT> =
        Link.OptionalToMany(first = this, last = next)


infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.SingleToSingle<FROM, TO>.link(next: Connection.SingleToSingle<TO, NEXT>): Connection.SingleToSingle<FROM, NEXT> =
        Link.SingleToSingle(first = this, last = next)


infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.FromSingle<FROM, TO>.link(next: Connection.SingleToMany<TO, NEXT>): Connection.SingleToMany<FROM, NEXT> =
        Link.SingleToMany(first = this, last = next)
infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.SingleToMany<FROM, TO>.link(next: Connection.FromSingle<TO, NEXT>): Connection.SingleToMany<FROM, NEXT> =
        Link.SingleToMany(first = this, last = next)


infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.ManyToOptional<FROM, TO>.link(next: Connection.ToOne<TO, NEXT>): Connection.ManyToOptional<FROM, NEXT> =
        Link.ManyToOptional(first = this, last = next)
infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.ManyToSingle<FROM, TO>.link(next: Connection.ToOptional<TO, NEXT>): Connection.ManyToOptional<FROM, NEXT> =
        Link.ManyToOptional(first = this, last = next)
infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.ToOne<FROM, TO>.link(next: Connection.ManyToOptional<TO, NEXT>): Connection.ManyToOptional<FROM, NEXT> =
        Link.ManyToOptional(first = this, last = next)


infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.ManyToSingle<FROM, TO>.link(next: Connection.ToSingle<TO, NEXT>): Connection.ManyToSingle<FROM, NEXT> =
        Link.ManyToSingle(first = this, last = next)
infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.ToSingle<FROM, TO>.link(next: Connection.ManyToSingle<TO, NEXT>): Connection.ManyToSingle<FROM, NEXT> =
        Link.ManyToSingle(first = this, last = next)


infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.FromMany<FROM, TO>.link(next: Connection.ToMany<TO, NEXT>): Connection.ManyToMany<FROM, NEXT> =
        Link.ManyToMany(first = this, last = next)
infix fun <FROM : Any, TO : Any, NEXT : Any> Connection<FROM, TO>.link(next: Connection.ManyToMany<TO, NEXT>): Connection.ManyToMany<FROM, NEXT> =
        Link.ManyToMany(first = this, last = next)
infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.ManyToMany<FROM, TO>.link(next: Connection<TO, NEXT>): Connection.ManyToMany<FROM, NEXT> =
        Link.ManyToMany(first = this, last = next)
infix fun <FROM : Any, TO : Any, NEXT : Any> Connection.ManyToMany<FROM, TO>.link(next: Connection.ManyToMany<TO, NEXT>): Connection.ManyToMany<FROM, NEXT> =
        Link.ManyToMany(first = this, last = next)
