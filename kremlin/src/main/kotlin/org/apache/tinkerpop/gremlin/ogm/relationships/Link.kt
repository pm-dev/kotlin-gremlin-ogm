package org.apache.tinkerpop.gremlin.ogm.relationships

import kotlin.reflect.KClass

/**
 * Links two [Connection]s as one.
 */
internal interface Link<FROM : Any, MIDDLE: Any, TO : Any>: Connection<FROM, TO> {

    val first: Connection<FROM, MIDDLE>

    val last: Connection<MIDDLE, TO>

    override val inverse: Link<TO, MIDDLE, FROM>

    override fun relationships(): List<Relationship<*, *>> = first.relationships() + last.relationships()

    override val fromKClass: KClass<FROM> get() = first.fromKClass

    override val toKClass: KClass<TO> get() = last.toKClass

    class OptionalToOptional<FROM : Any, MIDDLE : Any, TO : Any>(
            override val first: Connection.OneToOne<FROM, MIDDLE>,
            override val last: Connection.OneToOne<MIDDLE, TO>
    ) : Link<FROM, MIDDLE, TO>, Connection.OptionalToOptional<FROM, TO> {

        override val inverse: OptionalToOptional<TO, MIDDLE, FROM>
            get() = OptionalToOptional(first = last.inverse, last = first.inverse)
    }

    class OptionalToSingle<FROM : Any, MIDDLE: Any, TO : Any>(
            override val first: Connection.OneToSingle<FROM, MIDDLE>,
            override val last: Connection.OneToSingle<MIDDLE, TO>
    ) : Link<FROM, MIDDLE, TO>, Connection.OptionalToSingle<FROM, TO> {

        override val inverse: SingleToOptional<TO, MIDDLE, FROM>
            get() = SingleToOptional(first = last.inverse, last = first.inverse)
    }

    class SingleToOptional<FROM : Any, MIDDLE: Any, TO : Any>(
            override val first: Connection.SingleToOne<FROM, MIDDLE>,
            override val last: Connection.SingleToOne<MIDDLE, TO>
    ) : Link<FROM, MIDDLE, TO>, Connection.SingleToOptional<FROM, TO> {

        override val inverse: OptionalToSingle<TO, MIDDLE, FROM>
            get() = OptionalToSingle(first = last.inverse, last = first.inverse)
    }

    class SingleToSingle<FROM : Any, MIDDLE: Any, TO : Any>(
            override val first: Connection.SingleToSingle<FROM, MIDDLE>,
            override val last: Connection.SingleToSingle<MIDDLE, TO>
    ) : Link<FROM, MIDDLE, TO>, Connection.SingleToSingle<FROM, TO> {

        override val inverse: SingleToSingle<TO, MIDDLE, FROM>
            get() = SingleToSingle(first = last.inverse, last = first.inverse)
    }

    class OptionalToMany<FROM : Any, MIDDLE: Any, TO : Any>(
            override val first: Connection.FromOne<FROM, MIDDLE>,
            override val last: Connection.FromOne<MIDDLE, TO>
    ) : Link<FROM, MIDDLE, TO>, Connection.OptionalToMany<FROM, TO> {

        override val inverse: ManyToOptional<TO, MIDDLE, FROM>
            get() = ManyToOptional(first = last.inverse, last = first.inverse)
    }

    class SingleToMany<FROM : Any, MIDDLE: Any, TO : Any>(
            override val first: Connection.FromSingle<FROM, MIDDLE>,
            override val last: Connection.FromSingle<MIDDLE, TO>
    ) : Link<FROM, MIDDLE, TO>, Connection.SingleToMany<FROM, TO> {

        override val inverse: ManyToSingle<TO, MIDDLE, FROM>
            get() = ManyToSingle(first = last.inverse, last = first.inverse)
    }

    class ManyToOptional<FROM : Any, MIDDLE: Any, TO : Any>(
            override val first: Connection.ToOne<FROM, MIDDLE>,
            override val last: Connection.ToOne<MIDDLE, TO>
    ) : Link<FROM, MIDDLE, TO>, Connection.ManyToOptional<FROM, TO> {

        override val inverse: OptionalToMany<TO, MIDDLE, FROM>
            get() = OptionalToMany(first = last.inverse, last = first.inverse)
    }

    class ManyToSingle<FROM : Any, MIDDLE: Any, TO : Any>(
            override val first: Connection.ToSingle<FROM, MIDDLE>,
            override val last: Connection.ToSingle<MIDDLE, TO>
    ) : Link<FROM, MIDDLE, TO>, Connection.ManyToSingle<FROM, TO> {

        override val inverse: SingleToMany<TO, MIDDLE, FROM>
            get() = SingleToMany(first = last.inverse, last = first.inverse)
    }

    class ManyToMany<FROM : Any, MIDDLE: Any, TO : Any>(
            override val first: Connection<FROM, MIDDLE>,
            override val last: Connection<MIDDLE, TO>
    ) : Link<FROM, MIDDLE, TO>, Connection.ManyToMany<FROM, TO> {

        override val inverse: ManyToMany<TO, MIDDLE, FROM>
            get() = ManyToMany(first = last.inverse, last = first.inverse)
    }
}

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
