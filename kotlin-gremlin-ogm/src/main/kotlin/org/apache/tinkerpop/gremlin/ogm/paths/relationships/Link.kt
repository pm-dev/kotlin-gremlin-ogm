package org.apache.tinkerpop.gremlin.ogm.paths.relationships

/**
 * Links two [Connection]s as one.
 */
internal interface Link<FROM : Any, MIDDLE: Any, TO : Any>: Connection<FROM, TO> {

    /**
     * The first half of the linked connection.
     */
    val first: Connection<FROM, MIDDLE>

    /**
     * The second half of the linked connection.
     */
    val last: Connection<MIDDLE, TO>

    override val inverse: Link<TO, MIDDLE, FROM>

    override fun relationships(): List<Relationship<*, *>> = first.relationships() + last.relationships()

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
