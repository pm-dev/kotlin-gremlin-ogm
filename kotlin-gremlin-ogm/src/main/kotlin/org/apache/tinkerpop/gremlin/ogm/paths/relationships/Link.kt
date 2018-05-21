package org.apache.tinkerpop.gremlin.ogm.paths.relationships

/**
 * Links two [Connection]s as one.
 */
internal interface Link<OUT : Any, MIDDLE: Any, IN : Any>: Connection<OUT, IN> {

    /**
     * The first half of the linked connection.
     */
    val first: Connection<OUT, MIDDLE>

    /**
     * The second half of the linked connection.
     */
    val last: Connection<MIDDLE, IN>

    override val inverse: Link<IN, MIDDLE, OUT>

    override fun relationships(): List<Relationship<*, *>> = first.relationships() + last.relationships()

    class OptionalToOptional<OUT : Any, MIDDLE : Any, IN : Any>(
            override val first: Connection.OneToOne<OUT, MIDDLE>,
            override val last: Connection.OneToOne<MIDDLE, IN>
    ) : Link<OUT, MIDDLE, IN>, Connection.OptionalToOptional<OUT, IN> {

        override val inverse: OptionalToOptional<IN, MIDDLE, OUT>
            get() = OptionalToOptional(first = last.inverse, last = first.inverse)
    }

    class OptionalToSingle<OUT : Any, MIDDLE: Any, IN : Any>(
            override val first: Connection.OneToSingle<OUT, MIDDLE>,
            override val last: Connection.OneToSingle<MIDDLE, IN>
    ) : Link<OUT, MIDDLE, IN>, Connection.OptionalToSingle<OUT, IN> {

        override val inverse: SingleToOptional<IN, MIDDLE, OUT>
            get() = SingleToOptional(first = last.inverse, last = first.inverse)
    }

    class SingleToOptional<OUT : Any, MIDDLE: Any, IN : Any>(
            override val first: Connection.SingleToOne<OUT, MIDDLE>,
            override val last: Connection.SingleToOne<MIDDLE, IN>
    ) : Link<OUT, MIDDLE, IN>, Connection.SingleToOptional<OUT, IN> {

        override val inverse: OptionalToSingle<IN, MIDDLE, OUT>
            get() = OptionalToSingle(first = last.inverse, last = first.inverse)
    }

    class SingleToSingle<OUT : Any, MIDDLE: Any, IN : Any>(
            override val first: Connection.SingleToSingle<OUT, MIDDLE>,
            override val last: Connection.SingleToSingle<MIDDLE, IN>
    ) : Link<OUT, MIDDLE, IN>, Connection.SingleToSingle<OUT, IN> {

        override val inverse: SingleToSingle<IN, MIDDLE, OUT>
            get() = SingleToSingle(first = last.inverse, last = first.inverse)
    }

    class OptionalToMany<OUT : Any, MIDDLE: Any, IN : Any>(
            override val first: Connection.FromOne<OUT, MIDDLE>,
            override val last: Connection.FromOne<MIDDLE, IN>
    ) : Link<OUT, MIDDLE, IN>, Connection.OptionalToMany<OUT, IN> {

        override val inverse: ManyToOptional<IN, MIDDLE, OUT>
            get() = ManyToOptional(first = last.inverse, last = first.inverse)
    }

    class SingleToMany<OUT : Any, MIDDLE: Any, IN : Any>(
            override val first: Connection.FromSingle<OUT, MIDDLE>,
            override val last: Connection.FromSingle<MIDDLE, IN>
    ) : Link<OUT, MIDDLE, IN>, Connection.SingleToMany<OUT, IN> {

        override val inverse: ManyToSingle<IN, MIDDLE, OUT>
            get() = ManyToSingle(first = last.inverse, last = first.inverse)
    }

    class ManyToOptional<OUT : Any, MIDDLE: Any, IN : Any>(
            override val first: Connection.ToOne<OUT, MIDDLE>,
            override val last: Connection.ToOne<MIDDLE, IN>
    ) : Link<OUT, MIDDLE, IN>, Connection.ManyToOptional<OUT, IN> {

        override val inverse: OptionalToMany<IN, MIDDLE, OUT>
            get() = OptionalToMany(first = last.inverse, last = first.inverse)
    }

    class ManyToSingle<OUT : Any, MIDDLE: Any, IN : Any>(
            override val first: Connection.ToSingle<OUT, MIDDLE>,
            override val last: Connection.ToSingle<MIDDLE, IN>
    ) : Link<OUT, MIDDLE, IN>, Connection.ManyToSingle<OUT, IN> {

        override val inverse: SingleToMany<IN, MIDDLE, OUT>
            get() = SingleToMany(first = last.inverse, last = first.inverse)
    }

    class ManyToMany<OUT : Any, MIDDLE: Any, IN : Any>(
            override val first: Connection<OUT, MIDDLE>,
            override val last: Connection<MIDDLE, IN>
    ) : Link<OUT, MIDDLE, IN>, Connection.ManyToMany<OUT, IN> {

        override val inverse: ManyToMany<IN, MIDDLE, OUT>
            get() = ManyToMany(first = last.inverse, last = first.inverse)
    }
}
