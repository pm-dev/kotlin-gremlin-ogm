package org.apache.tinkerpop.gremlin.ogm.paths.relationships


/**
 * The 'link' function can be used to Combine two [Connection]s into one.
 */

infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.SingleToOne<OUT, IN>.link(next: Connection.OptionalToOptional<IN, NEXT>): Connection.OptionalToOptional<OUT, NEXT> =
        Link.OptionalToOptional(first = this, last = next)
infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.OptionalToOne<OUT, IN>.link(next: Connection.OneToOptional<IN, NEXT>): Connection.OptionalToOptional<OUT, NEXT> =
        Link.OptionalToOptional(first = this, last = next)
infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.OptionalToOptional<OUT, IN>.link(next: Connection.OneToSingle<IN, NEXT>): Connection.OptionalToOptional<OUT, NEXT> =
        Link.OptionalToOptional(first = this, last = next)


infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.OptionalToSingle<OUT, IN>.link(next: Connection.OneToSingle<IN, NEXT>): Connection.OptionalToSingle<OUT, NEXT> =
        Link.OptionalToSingle(first = this, last = next)
infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.OneToSingle<OUT, IN>.link(next: Connection.OptionalToSingle<IN, NEXT>): Connection.OptionalToSingle<OUT, NEXT> =
        Link.OptionalToSingle(first = this, last = next)


infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.SingleToOptional<OUT, IN>.link(next: Connection.SingleToOne<IN, NEXT>): Connection.SingleToOptional<OUT, NEXT> =
        Link.SingleToOptional(first = this, last = next)
infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.SingleToSingle<OUT, IN>.link(next: Connection.SingleToOptional<IN, NEXT>): Connection.SingleToOptional<OUT, NEXT> =
        Link.SingleToOptional(first = this, last = next)


infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.FromOne<OUT, IN>.link(next: Connection.OptionalToMany<IN, NEXT>): Connection.OptionalToMany<OUT, NEXT> =
        Link.OptionalToMany(first = this, last = next)
infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.FromOptional<OUT, IN>.link(next: Connection.SingleToMany<IN, NEXT>): Connection.OptionalToMany<OUT, NEXT> =
        Link.OptionalToMany(first = this, last = next)
infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.OptionalToMany<OUT, IN>.link(next: Connection.FromOne<IN, NEXT>): Connection.OptionalToMany<OUT, NEXT> =
        Link.OptionalToMany(first = this, last = next)


infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.SingleToSingle<OUT, IN>.link(next: Connection.SingleToSingle<IN, NEXT>): Connection.SingleToSingle<OUT, NEXT> =
        Link.SingleToSingle(first = this, last = next)


infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.FromSingle<OUT, IN>.link(next: Connection.SingleToMany<IN, NEXT>): Connection.SingleToMany<OUT, NEXT> =
        Link.SingleToMany(first = this, last = next)
infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.SingleToMany<OUT, IN>.link(next: Connection.FromSingle<IN, NEXT>): Connection.SingleToMany<OUT, NEXT> =
        Link.SingleToMany(first = this, last = next)


infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.ManyToOptional<OUT, IN>.link(next: Connection.ToOne<IN, NEXT>): Connection.ManyToOptional<OUT, NEXT> =
        Link.ManyToOptional(first = this, last = next)
infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.ManyToSingle<OUT, IN>.link(next: Connection.ToOptional<IN, NEXT>): Connection.ManyToOptional<OUT, NEXT> =
        Link.ManyToOptional(first = this, last = next)
infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.ToOne<OUT, IN>.link(next: Connection.ManyToOptional<IN, NEXT>): Connection.ManyToOptional<OUT, NEXT> =
        Link.ManyToOptional(first = this, last = next)


infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.ManyToSingle<OUT, IN>.link(next: Connection.ToSingle<IN, NEXT>): Connection.ManyToSingle<OUT, NEXT> =
        Link.ManyToSingle(first = this, last = next)
infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.ToSingle<OUT, IN>.link(next: Connection.ManyToSingle<IN, NEXT>): Connection.ManyToSingle<OUT, NEXT> =
        Link.ManyToSingle(first = this, last = next)


infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.FromMany<OUT, IN>.link(next: Connection.ToMany<IN, NEXT>): Connection.ManyToMany<OUT, NEXT> =
        Link.ManyToMany(first = this, last = next)
infix fun <OUT : Any, IN : Any, NEXT : Any> Connection<OUT, IN>.link(next: Connection.ManyToMany<IN, NEXT>): Connection.ManyToMany<OUT, NEXT> =
        Link.ManyToMany(first = this, last = next)
infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.ManyToMany<OUT, IN>.link(next: Connection<IN, NEXT>): Connection.ManyToMany<OUT, NEXT> =
        Link.ManyToMany(first = this, last = next)
infix fun <OUT : Any, IN : Any, NEXT : Any> Connection.ManyToMany<OUT, IN>.link(next: Connection.ManyToMany<IN, NEXT>): Connection.ManyToMany<OUT, NEXT> =
        Link.ManyToMany(first = this, last = next)
