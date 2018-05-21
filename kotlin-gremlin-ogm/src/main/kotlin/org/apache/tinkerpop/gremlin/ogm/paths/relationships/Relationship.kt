package org.apache.tinkerpop.gremlin.ogm.paths.relationships

/**
 * A [Relationship] defines a path between two vertices that does not travel through any other vertices.
 * Each [Relationship] must be registered with a GraphMapper.
 */
interface Relationship<OUT : Any, IN : Any> : Connection<OUT, IN> {

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

    override val inverse: Relationship<IN, OUT>

    data class SymmetricSingleToSingle<TYPE : Any>(
            override val name: String
    ) : SingleToSingle<TYPE, TYPE>, Symmetric<TYPE> {

        override val inverse: SymmetricSingleToSingle<TYPE> get() = this
    }

    data class SymmetricOptionalToOptional<TYPE : Any>(
            override val name: String
    ) : OptionalToOptional<TYPE, TYPE>, Symmetric<TYPE> {

        override val inverse: SymmetricOptionalToOptional<TYPE> get() = this
    }

    data class SymmetricManyToMany<TYPE : Any>(
            override val name: String
    ) : ManyToMany<TYPE, TYPE>, Symmetric<TYPE> {

        override val inverse: SymmetricManyToMany<TYPE> get() = this
    }

    data class AsymmetricOptionalToOptional<OUT : Any, IN : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : OptionalToOptional<OUT, IN> {

        override val inverse: AsymmetricOptionalToOptional<IN, OUT>
            get() = AsymmetricOptionalToOptional(
                    name = name,
                    direction = direction.inverse)
    }

    data class AsymmetricOptionalToSingle<OUT : Any, IN : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : OptionalToSingle<OUT, IN> {

        override val inverse: AsymmetricSingleToOptional<IN, OUT>
            get() = AsymmetricSingleToOptional(
                    name = name,
                    direction = direction.inverse)
    }

    data class AsymmetricSingleToOptional<OUT : Any, IN : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : SingleToOptional<OUT, IN> {

        override val inverse: AsymmetricOptionalToSingle<IN, OUT>
            get() = AsymmetricOptionalToSingle(
                    name = name,
                    direction = direction.inverse)
    }

    data class AsymmetricSingleToSingle<OUT : Any, IN : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : SingleToSingle<OUT, IN> {

        override val inverse: AsymmetricSingleToSingle<IN, OUT>
            get() = AsymmetricSingleToSingle(
                    name = name,
                    direction = direction.inverse)
    }

    data class AsymmetricSingleToMany<OUT : Any, IN : Any>(
            override val name: String
    ) : SingleToMany<OUT, IN>, AsymmetricOneToMany<OUT, IN> {

        override val inverse: AsymmetricManyToSingle<IN, OUT>
            get() = AsymmetricManyToSingle(
                    name = name)
    }

    data class AsymmetricOptionalToMany<OUT : Any, IN : Any>(
            override val name: String
    ) :  OptionalToMany<OUT, IN>, AsymmetricOneToMany<OUT, IN> {

        override val inverse: AsymmetricManyToOptional<IN, OUT>
            get() = AsymmetricManyToOptional(
                    name = name)
    }

    /**
     * We restrict creating ManyToOne relationships by clients to prevent creation of a
     * ManyToOne relationship that is equivalent in meaning to an already defined OneToMany
     * relationship, but using a different name. To get a ManyToOne relationship, define it
     * as its OneToMany equivalent then get its inverse.
     */
    data class AsymmetricManyToOptional<OUT : Any, IN : Any> internal constructor(
            override val name: String
    ) : ManyToOptional<OUT, IN>, AsymmetricManyToOne<OUT, IN> {

        override val inverse: AsymmetricOptionalToMany<IN, OUT>
            get() = AsymmetricOptionalToMany(
                    name = name)
    }

    /**
     * We restrict creating ManyToOne relationships by clients to prevent creation of a
     * ManyToOne relationship that is equivalent in meaning to an already defined OneToMany
     * relationship, but using a different name. To get a ManyToOne relationship, define it
     * as its OneToMany equivalent then get its inverse.
     */
    data class AsymmetricManyToSingle<OUT : Any, IN : Any> internal constructor(
            override val name: String
    ) : ManyToSingle<OUT, IN>, AsymmetricManyToOne<OUT, IN> {

        override val inverse: AsymmetricSingleToMany<IN, OUT>
            get() = AsymmetricSingleToMany(
                    name = name)
    }

    data class AsymmetricManyToMany<OUT : Any, IN : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : ManyToMany<OUT, IN> {

        override val inverse: AsymmetricManyToMany<IN, OUT>
            get() = AsymmetricManyToMany(
                    name = name,
                    direction = direction.inverse)
    }

    interface FromOne<OUT : Any, IN : Any> : Relationship<OUT, IN>, Connection.FromOne<OUT, IN> {

        override val inverse: ToOne<IN, OUT>
    }

    interface FromOptional<OUT : Any, IN : Any> : FromOne<OUT, IN>, Connection.FromOptional<OUT, IN> {

        override val inverse: ToOptional<IN, OUT>
    }

    interface FromSingle<OUT : Any, IN : Any> : FromOne<OUT, IN>, Connection.FromSingle<OUT, IN> {

        override val inverse: ToSingle<IN, OUT>
    }

    interface FromMany<OUT : Any, IN : Any> : Relationship<OUT, IN>, Connection.FromMany<OUT, IN> {

        override val inverse: ToMany<IN, OUT>
    }

    interface ToOne<OUT : Any, IN : Any> : Relationship<OUT, IN>, Connection.ToOne<OUT, IN> {

        override val inverse: FromOne<IN, OUT>
    }

    interface ToOptional<OUT : Any, IN : Any> : ToOne<OUT, IN>, Connection.ToOptional<OUT, IN> {

        override val inverse: FromOptional<IN, OUT>
    }

    interface ToSingle<OUT : Any, IN : Any> : ToOne<OUT, IN>, Connection.ToSingle<OUT, IN> {

        override val inverse: FromSingle<IN, OUT>
    }

    interface ToMany<OUT : Any, IN : Any> : Relationship<OUT, IN>, Connection.ToMany<OUT, IN> {

        override val inverse: FromMany<IN, OUT>
    }

    interface OneToOne<OUT : Any, IN : Any> : FromOne<OUT, IN>, ToOne<OUT, IN>, Connection.OneToOne<OUT, IN> {

        override val inverse: OneToOne<IN, OUT>
    }

    interface OneToOptional<OUT : Any, IN : Any> : OneToOne<OUT, IN>, ToOptional<OUT, IN>, Connection.OneToOptional<OUT, IN> {

        override val inverse: OptionalToOne<IN, OUT>
    }

    interface OneToSingle<OUT : Any, IN : Any> : OneToOne<OUT, IN>, ToSingle<OUT, IN>, Connection.OneToSingle<OUT, IN> {

        override val inverse: SingleToOne<IN, OUT>
    }

    interface OptionalToOne<OUT : Any, IN : Any> : FromOptional<OUT, IN>, OneToOne<OUT, IN>, Connection.OptionalToOne<OUT, IN> {

        override val inverse: OneToOptional<IN, OUT>
    }

    interface SingleToOne<OUT : Any, IN : Any> : FromSingle<OUT, IN>, OneToOne<OUT, IN>, Connection.SingleToOne<OUT, IN> {

        override val inverse: OneToSingle<IN, OUT>
    }

    interface OneToMany<OUT : Any, IN : Any> : FromOne<OUT, IN>, ToMany<OUT, IN>, Connection.OneToMany<OUT, IN> {

        override val inverse: ManyToOne<IN, OUT>
    }

    interface ManyToOne<OUT : Any, IN : Any> : FromMany<OUT, IN>, ToOne<OUT, IN>, Connection.ManyToOne<OUT, IN> {

        override val inverse: OneToMany<IN, OUT>
    }

    interface OptionalToOptional<OUT : Any, IN : Any> : OptionalToOne<OUT, IN>, OneToOptional<OUT, IN>, Connection.OptionalToOptional<OUT, IN> {

        override val inverse: OptionalToOptional<IN, OUT>
    }

    interface OptionalToSingle<OUT : Any, IN : Any> : OptionalToOne<OUT, IN>, OneToSingle<OUT, IN>, Connection.OptionalToSingle<OUT, IN> {

        override val inverse: SingleToOptional<IN, OUT>
    }

    interface SingleToOptional<OUT : Any, IN : Any> : SingleToOne<OUT, IN>, OneToOptional<OUT, IN>, Connection.SingleToOptional<OUT, IN> {

        override val inverse: OptionalToSingle<IN, OUT>
    }

    interface SingleToSingle<OUT : Any, IN : Any> : SingleToOne<OUT, IN>, OneToSingle<OUT, IN>, Connection.SingleToSingle<OUT, IN> {

        override val inverse: SingleToSingle<IN, OUT>
    }

    interface OptionalToMany<OUT : Any, IN : Any> : FromOptional<OUT, IN>, OneToMany<OUT, IN>, Connection.OptionalToMany<OUT, IN> {

        override val inverse: ManyToOptional<IN, OUT>
    }

    interface SingleToMany<OUT : Any, IN : Any> : FromSingle<OUT, IN>, OneToMany<OUT, IN>, Connection.SingleToMany<OUT, IN> {

        override val inverse: ManyToSingle<IN, OUT>
    }

    interface ManyToOptional<OUT : Any, IN : Any> : ManyToOne<OUT, IN>, ToOptional<OUT, IN>, Connection.ManyToOptional<OUT, IN> {

        override val inverse: OptionalToMany<IN, OUT>
    }

    interface ManyToSingle<OUT : Any, IN : Any> : ManyToOne<OUT, IN>, ToSingle<OUT, IN>, Connection.ManyToSingle<OUT, IN> {

        override val inverse: SingleToMany<IN, OUT>
    }

    interface ManyToMany<OUT : Any, IN : Any> : FromMany<OUT, IN>, ToMany<OUT, IN>, Connection.ManyToMany<OUT, IN> {

        override val inverse: ManyToMany<IN, OUT>
    }

    interface Symmetric<TYPE : Any> : Relationship<TYPE, TYPE> {

        override val direction: Direction? get() = null
    }

    interface AsymmetricManyToOne<OUT : Any, IN : Any> : ManyToOne<OUT, IN> {

        override val direction: Direction? get() = Direction.BACKWARD
    }

    interface AsymmetricOneToMany<OUT : Any, IN : Any> : OneToMany<OUT, IN> {

        override val direction: Direction? get() = Direction.FORWARD
    }

    companion object {

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'OUT' object,
         * there will be 0 or 1 'IN' objects. When the [inverse] is traversed from a 'IN' object,
         * there will be 0 or 1 'OUT' objects.
         */
        inline fun <reified OUT : Any, reified IN : Any> asymmetricOptionalToOptional(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricOptionalToOptional<OUT, IN>(
                name = name,
                direction = direction)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'OUT' object,
         * there will be exactly 1 'IN' objects. When the [inverse] is traversed from a 'IN' object,
         * there will be 0 or 1 'OUT' objects.
         */
        inline fun <reified OUT : Any, reified IN : Any> asymmetricOptionalToSingle(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricOptionalToSingle<OUT, IN>(
                name = name,
                direction = direction)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'OUT' object,
         * there will be 0 or 1 'IN' objects. When the [inverse] is traversed from a 'IN' object,
         * there will be exactly 1 'OUT' object.
         */
        inline fun <reified OUT : Any, reified IN : Any> asymmetricSingleToOptional(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricSingleToOptional<OUT, IN>(
                name = name,
                direction = direction)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'OUT' object,
         * there will be exactly 1 'IN' object. When the [inverse] is traversed from a 'IN' object,
         * there will be exactly 1 'OUT' object.
         */
        inline fun <reified OUT : Any, reified IN : Any> asymmetricSingleToSingle(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricSingleToSingle<OUT, IN>(
                name = name,
                direction = direction)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'OUT' object,
         * there will be exactly 1 'IN' object. When the [inverse] is traversed from a 'IN' object,
         * there will be exactly 1 'OUT' object.
         */
        inline fun <reified OUT : Any, reified IN : Any> asymmetricSingleToMany(
                name: String
        ) = AsymmetricSingleToMany<OUT, IN>(
                name = name)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'OUT' object,
         * there will be 0 or more 'IN' objects. When the [inverse] is traversed from a 'IN' object,
         * there will be 0 or 1 'OUT' object.
         */
        inline fun <reified OUT : Any, reified IN : Any> asymmetricOptionalToMany(
                name: String
        ) = AsymmetricOptionalToMany<OUT, IN>(
                name = name)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'OUT' object,
         * there will be 0 or more 'IN' objects. When the [inverse] is traversed from a 'IN' object,
         * there will be 0 or more 'OUT' objects.
         */
        inline fun <reified OUT : Any, reified IN : Any> asymmetricManyToMany(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricManyToMany<OUT, IN>(
                name = name,
                direction = direction)

        /**
         * Creates a [Relationship] that is bi-directional. When traversed from a 'OUT' object,
         * there will be 0 or 1 'IN' objects. When the [inverse] is traversed from a 'IN' object,
         * there will be 0 or 1 'OUT' objects.
         */
        inline fun <reified TYPE : Any> symmetricOptionalToOptional(
                name: String
        ) = SymmetricOptionalToOptional<TYPE>(
                name = name)

        /**
         * Creates a [Relationship] that is bi-directional. When traversed from a 'OUT' object,
         * there will be exactly 1 'IN' object. When the [inverse] is traversed from a 'IN' object,
         * there will be exactly 1 'OUT' object.
         */
        inline fun <reified TYPE : Any> symmetricSingleToSingle(
                name: String
        ) = SymmetricSingleToSingle<TYPE>(
                name = name)

        /**
         * Creates a [Relationship] that is bi-directional. When traversed from a 'OUT' object,
         * there will be 0 or more 'IN' objects. When the [inverse] is traversed from a 'IN' object,
         * there will be 0 or more 'OUT' objects.
         */
        inline fun <reified TYPE : Any> symmetricManyToMany(
                name: String
        ) = SymmetricManyToMany<TYPE>(
                name = name)
    }
}
