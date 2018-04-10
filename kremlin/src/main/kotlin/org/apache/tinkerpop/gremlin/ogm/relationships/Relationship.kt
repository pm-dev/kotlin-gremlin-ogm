package org.apache.tinkerpop.gremlin.ogm.relationships

import kotlin.reflect.KClass

/**
 * A [Relationship] defines a path between two vertices that does not travel through any other vertices.
 * A [Relationship] can be thought of as the schema for an Edge.
 */
interface Relationship<FROM : Any, TO : Any> : Connection<FROM, TO> {

    enum class Direction {
        FORWARD,
        BACKWARD;

        val inverse: Direction
            get() = when (this) {
                FORWARD -> BACKWARD
                BACKWARD -> FORWARD
            }
    }

    val name: String

    override fun relationships() = listOf(this)

    val direction: Direction?

    override val inverse: Relationship<TO, FROM>

    class SymmetricSingleToSingle<TYPE : Any>(
            override val name: String,
            override val vertexKClass: KClass<TYPE>
    ) : SingleToSingle<TYPE, TYPE>, Symmetric<TYPE> {

        override val inverse: SymmetricSingleToSingle<TYPE> get() = this
    }

    class SymmetricOptionalToOptional<TYPE : Any>(
            override val name: String,
            override val vertexKClass: KClass<TYPE>
    ) : OptionalToOptional<TYPE, TYPE>, Symmetric<TYPE> {

        override val inverse: SymmetricOptionalToOptional<TYPE> get() = this
    }

    class SymmetricManyToMany<TYPE : Any>(
            override val name: String,
            override val vertexKClass: KClass<TYPE>
    ) : ManyToMany<TYPE, TYPE>, Symmetric<TYPE> {

        override val inverse: SymmetricManyToMany<TYPE> get() = this
    }

    class AsymmetricOptionalToOptional<FROM : Any, TO : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD,
            override val fromKClass: KClass<FROM>,
            override val toKClass: KClass<TO>
    ) : OptionalToOptional<FROM, TO> {

        override val inverse: AsymmetricOptionalToOptional<TO, FROM>
            get() = AsymmetricOptionalToOptional(
                    fromKClass = toKClass,
                    toKClass = fromKClass,
                    name = name,
                    direction = direction.inverse)
    }

    class AsymmetricOptionalToSingle<FROM : Any, TO : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD,
            override val fromKClass: KClass<FROM>,
            override val toKClass: KClass<TO>
    ) : OptionalToSingle<FROM, TO> {

        override val inverse: AsymmetricSingleToOptional<TO, FROM>
            get() = AsymmetricSingleToOptional(
                    fromKClass = toKClass,
                    toKClass = fromKClass,
                    name = name,
                    direction = direction.inverse)
    }

    class AsymmetricSingleToOptional<FROM : Any, TO : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD,
            override val fromKClass: KClass<FROM>,
            override val toKClass: KClass<TO>
    ) : SingleToOptional<FROM, TO> {

        override val inverse: AsymmetricOptionalToSingle<TO, FROM>
            get() = AsymmetricOptionalToSingle(
                    fromKClass = toKClass,
                    toKClass = fromKClass,
                    name = name,
                    direction = direction.inverse)
    }

    class AsymmetricSingleToSingle<FROM : Any, TO : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD,
            override val fromKClass: KClass<FROM>,
            override val toKClass: KClass<TO>
    ) : SingleToSingle<FROM, TO> {

        override val inverse: AsymmetricSingleToSingle<TO, FROM>
            get() = AsymmetricSingleToSingle(
                    fromKClass = toKClass,
                    toKClass = fromKClass,
                    name = name,
                    direction = direction.inverse)
    }

    class AsymmetricSingleToMany<FROM : Any, TO : Any>(
            override val name: String,
            override val fromKClass: KClass<FROM>,
            override val toKClass: KClass<TO>
    ) : SingleToMany<FROM, TO>, AsymmetricOneToMany<FROM, TO> {

        override val inverse: AsymmetricManyToSingle<TO, FROM>
            get() = AsymmetricManyToSingle(
                    fromKClass = toKClass,
                    toKClass = fromKClass,
                    name = name)
    }

    class AsymmetricOptionalToMany<FROM : Any, TO : Any>(
            override val name: String,
            override val fromKClass: KClass<FROM>,
            override val toKClass: KClass<TO>
    ) :  OptionalToMany<FROM, TO>, AsymmetricOneToMany<FROM, TO> {

        override val inverse: AsymmetricManyToOptional<TO, FROM>
            get() = AsymmetricManyToOptional(
                    fromKClass = toKClass,
                    toKClass = fromKClass,
                    name = name)
    }

    /**
     * We restrict creating ManyToOne relationships by clients to prevent creation of a
     * ManyToOne relationship that is equivalent in meaning to an already defined OneToMany
     * relationship, but using a different name. To get a ManyToOne relationship, define it
     * as its OneToMany equivalent then get its inverse.
     */
    class AsymmetricManyToOptional<FROM : Any, TO : Any> internal constructor(
            override val name: String,
            override val fromKClass: KClass<FROM>,
            override val toKClass: KClass<TO>
    ) : ManyToOptional<FROM, TO>, AsymmetricManyToOne<FROM, TO> {

        override val inverse: AsymmetricOptionalToMany<TO, FROM>
            get() = AsymmetricOptionalToMany(
                    fromKClass = toKClass,
                    toKClass = fromKClass,
                    name = name)
    }

    /**
     * We restrict creating ManyToOne relationships by clients to prevent creation of a
     * ManyToOne relationship that is equivalent in meaning to an already defined OneToMany
     * relationship, but using a different name. To get a ManyToOne relationship, define it
     * as its OneToMany equivalent then get its inverse.
     */
    class AsymmetricManyToSingle<FROM : Any, TO : Any> internal constructor(
            override val name: String,
            override val fromKClass: KClass<FROM>,
            override val toKClass: KClass<TO>
    ) : ManyToSingle<FROM, TO>, AsymmetricManyToOne<FROM, TO> {

        override val inverse: AsymmetricSingleToMany<TO, FROM>
            get() = AsymmetricSingleToMany(
                    fromKClass = toKClass,
                    toKClass = fromKClass,
                    name = name)
    }

    class AsymmetricManyToMany<FROM : Any, TO : Any>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD,
            override val fromKClass: KClass<FROM>,
            override val toKClass: KClass<TO>
    ) : ManyToMany<FROM, TO> {

        override val inverse: AsymmetricManyToMany<TO, FROM>
            get() = AsymmetricManyToMany(
                    fromKClass = toKClass,
                    toKClass = fromKClass,
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

        val vertexKClass: KClass<TYPE>

        override val fromKClass: KClass<TYPE> get() = vertexKClass

        override val toKClass: KClass<TYPE> get() = vertexKClass

        override val direction: Direction? get() = null
    }

    interface AsymmetricManyToOne<FROM : Any, TO : Any> : ManyToOne<FROM, TO> {

        override val direction: Direction? get() = Direction.BACKWARD
    }

    interface AsymmetricOneToMany<FROM : Any, TO : Any> : OneToMany<FROM, TO> {

        override val direction: Direction? get() = Direction.FORWARD
    }

    companion object {

        inline fun <reified FROM : Any, reified TO : Any> asymmetricOptionalToOptional(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricOptionalToOptional(
                fromKClass = FROM::class,
                toKClass = TO::class,
                name = name,
                direction = direction)

        inline fun <reified FROM : Any, reified TO : Any> asymmetricOptionalToSingle(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricOptionalToSingle(
                fromKClass = FROM::class,
                toKClass = TO::class,
                name = name,
                direction = direction)

        inline fun <reified FROM : Any, reified TO : Any> asymmetricSingleToOptional(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricSingleToOptional(
                fromKClass = FROM::class,
                toKClass = TO::class,
                name = name,
                direction = direction)

        inline fun <reified FROM : Any, reified TO : Any> asymmetricSingleToSingle(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricSingleToSingle(
                fromKClass = FROM::class,
                toKClass = TO::class,
                name = name,
                direction = direction)

        inline fun <reified FROM : Any, reified TO : Any> asymmetricSingleToMany(
                name: String
        ) = AsymmetricSingleToMany(
                fromKClass = FROM::class,
                toKClass = TO::class,
                name = name)

        inline fun <reified FROM : Any, reified TO : Any> asymmetricOptionalToMany(
                name: String
        ) = AsymmetricOptionalToMany(
                fromKClass = FROM::class,
                toKClass = TO::class,
                name = name)

        inline fun <reified FROM : Any, reified TO : Any> asymmetricManyToMany(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricManyToMany(
                fromKClass = FROM::class,
                toKClass = TO::class,
                name = name,
                direction = direction)

        inline fun <reified TYPE : Any> symmetricOptionalToOptional(
                name: String
        ) = SymmetricOptionalToOptional(
                vertexKClass = TYPE::class,
                name = name)

        inline fun <reified TYPE : Any> symmetricSingleToSingle(
                name: String
        ) = SymmetricSingleToSingle(
                vertexKClass = TYPE::class,
                name = name)

        inline fun <reified TYPE : Any> symmetricManyToMany(
                name: String
        ) = SymmetricManyToMany(
                vertexKClass = TYPE::class,
                name = name)
    }
}
