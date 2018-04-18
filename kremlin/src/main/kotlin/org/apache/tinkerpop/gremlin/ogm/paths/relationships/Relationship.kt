package org.apache.tinkerpop.gremlin.ogm.paths.relationships

/**
 * A [Relationship] defines a path between two vertices that does not travel through any other vertices.
 */
interface Relationship<FROM : Any, TO : Any> : Connection<FROM, TO> {

    /**
     * Relationships may be asymmetric, meaning if vertex A relates to vertex B, that
     * does not imply B relates to A. Thus, a relationship name and two vertices is not
     * enough to represent the full semantics of a [Relationship], we also need [Direction]
     * to specify the origin and target of an asymmetric relationship.
     */
    enum class Direction {
        FORWARD,
        BACKWARD;

        val inverse: Direction
            get() = when (this) {
                FORWARD -> BACKWARD
                BACKWARD -> FORWARD
            }
    }

    /**
     * The name of the [Relationship] that will be stored in the graph. [Relationship.name]s
     * must be globally unique.
     */
    val name: String

    /**
     * The [Direction] of the [Relationship], or null if the [Relationship] is [Symmetric]
     */
    val direction: Direction?

    override fun relationships() = listOf(this)

    override val inverse: Relationship<TO, FROM>

    class SymmetricSingleToSingle<TYPE : Any>(
            override val name: String
    ) : SingleToSingle<TYPE, TYPE>, Symmetric<TYPE> {

        override val inverse: SymmetricSingleToSingle<TYPE> get() = this
    }

    class SymmetricOptionalToOptional<TYPE : Any>(
            override val name: String
    ) : OptionalToOptional<TYPE, TYPE>, Symmetric<TYPE> {

        override val inverse: SymmetricOptionalToOptional<TYPE> get() = this
    }

    class SymmetricManyToMany<TYPE : Any>(
            override val name: String
    ) : ManyToMany<TYPE, TYPE>, Symmetric<TYPE> {

        override val inverse: SymmetricManyToMany<TYPE> get() = this
    }

    class AsymmetricOptionalToOptional<FROM : Any, TO : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : OptionalToOptional<FROM, TO> {

        override val inverse: AsymmetricOptionalToOptional<TO, FROM>
            get() = AsymmetricOptionalToOptional(
                    name = name,
                    direction = direction.inverse)
    }

    class AsymmetricOptionalToSingle<FROM : Any, TO : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : OptionalToSingle<FROM, TO> {

        override val inverse: AsymmetricSingleToOptional<TO, FROM>
            get() = AsymmetricSingleToOptional(
                    name = name,
                    direction = direction.inverse)
    }

    class AsymmetricSingleToOptional<FROM : Any, TO : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : SingleToOptional<FROM, TO> {

        override val inverse: AsymmetricOptionalToSingle<TO, FROM>
            get() = AsymmetricOptionalToSingle(
                    name = name,
                    direction = direction.inverse)
    }

    class AsymmetricSingleToSingle<FROM : Any, TO : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : SingleToSingle<FROM, TO> {

        override val inverse: AsymmetricSingleToSingle<TO, FROM>
            get() = AsymmetricSingleToSingle(
                    name = name,
                    direction = direction.inverse)
    }

    class AsymmetricSingleToMany<FROM : Any, TO : Any>(
            override val name: String
    ) : SingleToMany<FROM, TO>, AsymmetricOneToMany<FROM, TO> {

        override val inverse: AsymmetricManyToSingle<TO, FROM>
            get() = AsymmetricManyToSingle(
                    name = name)
    }

    class AsymmetricOptionalToMany<FROM : Any, TO : Any>(
            override val name: String
    ) :  OptionalToMany<FROM, TO>, AsymmetricOneToMany<FROM, TO> {

        override val inverse: AsymmetricManyToOptional<TO, FROM>
            get() = AsymmetricManyToOptional(
                    name = name)
    }

    /**
     * We restrict creating ManyToOne relationships by clients to prevent creation of a
     * ManyToOne relationship that is equivalent in meaning to an already defined OneToMany
     * relationship, but using a different name. To get a ManyToOne relationship, define it
     * as its OneToMany equivalent then get its inverse.
     */
    class AsymmetricManyToOptional<FROM : Any, TO : Any> internal constructor(
            override val name: String
    ) : ManyToOptional<FROM, TO>, AsymmetricManyToOne<FROM, TO> {

        override val inverse: AsymmetricOptionalToMany<TO, FROM>
            get() = AsymmetricOptionalToMany(
                    name = name)
    }

    /**
     * We restrict creating ManyToOne relationships by clients to prevent creation of a
     * ManyToOne relationship that is equivalent in meaning to an already defined OneToMany
     * relationship, but using a different name. To get a ManyToOne relationship, define it
     * as its OneToMany equivalent then get its inverse.
     */
    class AsymmetricManyToSingle<FROM : Any, TO : Any> internal constructor(
            override val name: String
    ) : ManyToSingle<FROM, TO>, AsymmetricManyToOne<FROM, TO> {

        override val inverse: AsymmetricSingleToMany<TO, FROM>
            get() = AsymmetricSingleToMany(
                    name = name)
    }

    class AsymmetricManyToMany<FROM : Any, TO : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : ManyToMany<FROM, TO> {

        override val inverse: AsymmetricManyToMany<TO, FROM>
            get() = AsymmetricManyToMany(
                    name = name,
                    direction = direction.inverse)
    }

    interface FromOne<FROM : Any, TO : Any> : Relationship<FROM, TO>, Connection.FromOne<FROM, TO> {

        override val inverse: ToOne<TO, FROM>
    }

    interface FromOptional<FROM : Any, TO : Any> : FromOne<FROM, TO>, Connection.FromOptional<FROM, TO> {

        override val inverse: ToOptional<TO, FROM>
    }

    interface FromSingle<FROM : Any, TO : Any> : FromOne<FROM, TO>, Connection.FromSingle<FROM, TO> {

        override val inverse: ToSingle<TO, FROM>
    }

    interface FromMany<FROM : Any, TO : Any> : Relationship<FROM, TO>, Connection.FromMany<FROM, TO> {

        override val inverse: ToMany<TO, FROM>
    }

    interface ToOne<FROM : Any, TO : Any> : Relationship<FROM, TO>, Connection.ToOne<FROM, TO> {

        override val inverse: FromOne<TO, FROM>
    }

    interface ToOptional<FROM : Any, TO : Any> : ToOne<FROM, TO>, Connection.ToOptional<FROM, TO> {

        override val inverse: FromOptional<TO, FROM>
    }

    interface ToSingle<FROM : Any, TO : Any> : ToOne<FROM, TO>, Connection.ToSingle<FROM, TO> {

        override val inverse: FromSingle<TO, FROM>
    }

    interface ToMany<FROM : Any, TO : Any> : Relationship<FROM, TO>, Connection.ToMany<FROM, TO> {

        override val inverse: FromMany<TO, FROM>
    }

    interface OneToOne<FROM : Any, TO : Any> : FromOne<FROM, TO>, ToOne<FROM, TO>, Connection.OneToOne<FROM, TO> {

        override val inverse: OneToOne<TO, FROM>
    }

    interface OneToOptional<FROM : Any, TO : Any> : OneToOne<FROM, TO>, ToOptional<FROM, TO>, Connection.OneToOptional<FROM, TO> {

        override val inverse: OptionalToOne<TO, FROM>
    }

    interface OneToSingle<FROM : Any, TO : Any> : OneToOne<FROM, TO>, ToSingle<FROM, TO>, Connection.OneToSingle<FROM, TO> {

        override val inverse: SingleToOne<TO, FROM>
    }

    interface OptionalToOne<FROM : Any, TO : Any> : FromOptional<FROM, TO>, OneToOne<FROM, TO>, Connection.OptionalToOne<FROM, TO> {

        override val inverse: OneToOptional<TO, FROM>
    }

    interface SingleToOne<FROM : Any, TO : Any> : FromSingle<FROM, TO>, OneToOne<FROM, TO>, Connection.SingleToOne<FROM, TO> {

        override val inverse: OneToSingle<TO, FROM>
    }

    interface OneToMany<FROM : Any, TO : Any> : FromOne<FROM, TO>, ToMany<FROM, TO>, Connection.OneToMany<FROM, TO> {

        override val inverse: ManyToOne<TO, FROM>
    }

    interface ManyToOne<FROM : Any, TO : Any> : FromMany<FROM, TO>, ToOne<FROM, TO>, Connection.ManyToOne<FROM, TO> {

        override val inverse: OneToMany<TO, FROM>
    }

    interface OptionalToOptional<FROM : Any, TO : Any> : OptionalToOne<FROM, TO>, OneToOptional<FROM, TO>, Connection.OptionalToOptional<FROM, TO> {

        override val inverse: OptionalToOptional<TO, FROM>
    }

    interface OptionalToSingle<FROM : Any, TO : Any> : OptionalToOne<FROM, TO>, OneToSingle<FROM, TO>, Connection.OptionalToSingle<FROM, TO> {

        override val inverse: SingleToOptional<TO, FROM>
    }

    interface SingleToOptional<FROM : Any, TO : Any> : SingleToOne<FROM, TO>, OneToOptional<FROM, TO>, Connection.SingleToOptional<FROM, TO> {

        override val inverse: OptionalToSingle<TO, FROM>
    }

    interface SingleToSingle<FROM : Any, TO : Any> : SingleToOne<FROM, TO>, OneToSingle<FROM, TO>, Connection.SingleToSingle<FROM, TO> {

        override val inverse: SingleToSingle<TO, FROM>
    }

    interface OptionalToMany<FROM : Any, TO : Any> : FromOptional<FROM, TO>, OneToMany<FROM, TO>, Connection.OptionalToMany<FROM, TO> {

        override val inverse: ManyToOptional<TO, FROM>
    }

    interface SingleToMany<FROM : Any, TO : Any> : FromSingle<FROM, TO>, OneToMany<FROM, TO>, Connection.SingleToMany<FROM, TO> {

        override val inverse: ManyToSingle<TO, FROM>
    }

    interface ManyToOptional<FROM : Any, TO : Any> : ManyToOne<FROM, TO>, ToOptional<FROM, TO>, Connection.ManyToOptional<FROM, TO> {

        override val inverse: OptionalToMany<TO, FROM>
    }

    interface ManyToSingle<FROM : Any, TO : Any> : ManyToOne<FROM, TO>, ToSingle<FROM, TO>, Connection.ManyToSingle<FROM, TO> {

        override val inverse: SingleToMany<TO, FROM>
    }

    interface ManyToMany<FROM : Any, TO : Any> : FromMany<FROM, TO>, ToMany<FROM, TO>, Connection.ManyToMany<FROM, TO> {

        override val inverse: ManyToMany<TO, FROM>
    }

    interface Symmetric<TYPE : Any> : Relationship<TYPE, TYPE> {

        override val direction: Direction? get() = null
    }

    interface AsymmetricManyToOne<FROM : Any, TO : Any> : ManyToOne<FROM, TO> {

        override val direction: Direction? get() = Direction.BACKWARD
    }

    interface AsymmetricOneToMany<FROM : Any, TO : Any> : OneToMany<FROM, TO> {

        override val direction: Direction? get() = Direction.FORWARD
    }

    companion object {

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'FROM' object,
         * there will be 0 or 1 'TO' objects. When the [inverse] is traversed from a 'TO' object,
         * there will be 0 or 1 'FROM' objects.
         */
        inline fun <reified FROM : Any, reified TO : Any> asymmetricOptionalToOptional(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricOptionalToOptional<FROM, TO>(
                name = name,
                direction = direction)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'FROM' object,
         * there will be exactly 1 'TO' objects. When the [inverse] is traversed from a 'TO' object,
         * there will be 0 or 1 'FROM' objects.
         */
        inline fun <reified FROM : Any, reified TO : Any> asymmetricOptionalToSingle(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricOptionalToSingle<FROM, TO>(
                name = name,
                direction = direction)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'FROM' object,
         * there will be 0 or 1 'TO' objects. When the [inverse] is traversed from a 'TO' object,
         * there will be exactly 1 'FROM' object.
         */
        inline fun <reified FROM : Any, reified TO : Any> asymmetricSingleToOptional(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricSingleToOptional<FROM, TO>(
                name = name,
                direction = direction)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'FROM' object,
         * there will be exactly 1 'TO' object. When the [inverse] is traversed from a 'TO' object,
         * there will be exactly 1 'FROM' object.
         */
        inline fun <reified FROM : Any, reified TO : Any> asymmetricSingleToSingle(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricSingleToSingle<FROM, TO>(
                name = name,
                direction = direction)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'FROM' object,
         * there will be exactly 1 'TO' object. When the [inverse] is traversed from a 'TO' object,
         * there will be exactly 1 'FROM' object.
         */
        inline fun <reified FROM : Any, reified TO : Any> asymmetricSingleToMany(
                name: String
        ) = AsymmetricSingleToMany<FROM, TO>(
                name = name)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'FROM' object,
         * there will be 0 or more 'TO' objects. When the [inverse] is traversed from a 'TO' object,
         * there will be 0 or 1 'FROM' object.
         */
        inline fun <reified FROM : Any, reified TO : Any> asymmetricOptionalToMany(
                name: String
        ) = AsymmetricOptionalToMany<FROM, TO>(
                name = name)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'FROM' object,
         * there will be 0 or more 'TO' objects. When the [inverse] is traversed from a 'TO' object,
         * there will be 0 or more 'FROM' objects.
         */
        inline fun <reified FROM : Any, reified TO : Any> asymmetricManyToMany(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricManyToMany<FROM, TO>(
                name = name,
                direction = direction)

        /**
         * Creates a [Relationship] that is bi-directional. When traversed from a 'FROM' object,
         * there will be 0 or 1 'TO' objects. When the [inverse] is traversed from a 'TO' object,
         * there will be 0 or 1 'FROM' objects.
         */
        inline fun <reified TYPE : Any> symmetricOptionalToOptional(
                name: String
        ) = SymmetricOptionalToOptional<TYPE>(
                name = name)

        /**
         * Creates a [Relationship] that is bi-directional. When traversed from a 'FROM' object,
         * there will be exactly 1 'TO' object. When the [inverse] is traversed from a 'TO' object,
         * there will be exactly 1 'FROM' object.
         */
        inline fun <reified TYPE : Any> symmetricSingleToSingle(
                name: String
        ) = SymmetricSingleToSingle<TYPE>(
                name = name)

        /**
         * Creates a [Relationship] that is bi-directional. When traversed from a 'FROM' object,
         * there will be 0 or more 'TO' objects. When the [inverse] is traversed from a 'TO' object,
         * there will be 0 or more 'FROM' objects.
         */
        inline fun <reified TYPE : Any> symmetricManyToMany(
                name: String
        ) = SymmetricManyToMany<TYPE>(
                name = name)
    }
}
