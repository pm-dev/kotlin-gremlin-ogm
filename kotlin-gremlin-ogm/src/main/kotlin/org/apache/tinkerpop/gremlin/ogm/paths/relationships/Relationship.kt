package org.apache.tinkerpop.gremlin.ogm.paths.relationships

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex

/**
 * A [Relationship] defines a path between two vertices that does not travel through any other vertices.
 * Each [Relationship] must be registered with a GraphMapper.
 */
interface Relationship<FROM : Vertex, TO : Vertex> : Connection<FROM, TO> {

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
     * The name of the [Relationship] that will be stored to the graph. [Relationship.name]s
     * must be globally unique.
     */
    val name: String

    /**
     * The [Direction] of the [Relationship], or null if the [Relationship] is [Symmetric]
     */
    val direction: Direction?

    override fun relationships() = listOf(this)

    override val inverse: Relationship<TO, FROM>

    data class SymmetricSingleToSingle<TYPE : Vertex>(
            override val name: String
    ) : SingleToSingle<TYPE, TYPE>, Symmetric<TYPE> {

        override val inverse: SymmetricSingleToSingle<TYPE> get() = this
    }

    data class SymmetricOptionalToOptional<TYPE : Vertex>(
            override val name: String
    ) : OptionalToOptional<TYPE, TYPE>, Symmetric<TYPE> {

        override val inverse: SymmetricOptionalToOptional<TYPE> get() = this
    }

    data class SymmetricManyToMany<TYPE : Vertex>(
            override val name: String
    ) : ManyToMany<TYPE, TYPE>, Symmetric<TYPE> {

        override val inverse: SymmetricManyToMany<TYPE> get() = this
    }

    data class AsymmetricOptionalToOptional<FROM : Vertex, TO : Vertex>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : OptionalToOptional<FROM, TO> {

        override val inverse: AsymmetricOptionalToOptional<TO, FROM>
            get() = AsymmetricOptionalToOptional(
                    name = name,
                    direction = direction.inverse)
    }

    data class AsymmetricOptionalToSingle<FROM : Vertex, TO : Vertex>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : OptionalToSingle<FROM, TO> {

        override val inverse: AsymmetricSingleToOptional<TO, FROM>
            get() = AsymmetricSingleToOptional(
                    name = name,
                    direction = direction.inverse)
    }

    data class AsymmetricSingleToOptional<FROM : Vertex, TO : Vertex>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : SingleToOptional<FROM, TO> {

        override val inverse: AsymmetricOptionalToSingle<TO, FROM>
            get() = AsymmetricOptionalToSingle(
                    name = name,
                    direction = direction.inverse)
    }

    data class AsymmetricSingleToSingle<FROM : Vertex, TO : Vertex>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : SingleToSingle<FROM, TO> {

        override val inverse: AsymmetricSingleToSingle<TO, FROM>
            get() = AsymmetricSingleToSingle(
                    name = name,
                    direction = direction.inverse)
    }

    data class AsymmetricSingleToMany<FROM : Vertex, TO : Vertex>(
            override val name: String
    ) : SingleToMany<FROM, TO>, AsymmetricOneToMany<FROM, TO> {

        override val inverse: AsymmetricManyToSingle<TO, FROM>
            get() = AsymmetricManyToSingle(
                    name = name)
    }

    data class AsymmetricOptionalToMany<FROM : Vertex, TO : Vertex>(
            override val name: String
    ) : OptionalToMany<FROM, TO>, AsymmetricOneToMany<FROM, TO> {

        override val inverse: AsymmetricManyToOptional<TO, FROM>
            get() = AsymmetricManyToOptional(
                    name = name)
    }

    /**
     * We restrict creating ManyToOne relationships by clients to prevent creation of a
     * ManyToOne relationship that is equivalent to meaning to an already defined OneToMany
     * relationship, but using a different name. To get a ManyToOne relationship, define it
     * as its OneToMany equivalent then get its inverse.
     */
    data class AsymmetricManyToOptional<FROM : Vertex, TO : Vertex> internal constructor(
            override val name: String
    ) : ManyToOptional<FROM, TO>, AsymmetricManyToOne<FROM, TO> {

        override val inverse: AsymmetricOptionalToMany<TO, FROM>
            get() = AsymmetricOptionalToMany(
                    name = name)
    }

    /**
     * We restrict creating ManyToOne relationships by clients to prevent creation of a
     * ManyToOne relationship that is equivalent to meaning to an already defined OneToMany
     * relationship, but using a different name. To get a ManyToOne relationship, define it
     * as its OneToMany equivalent then get its inverse.
     */
    data class AsymmetricManyToSingle<FROM : Vertex, TO : Vertex> internal constructor(
            override val name: String
    ) : ManyToSingle<FROM, TO>, AsymmetricManyToOne<FROM, TO> {

        override val inverse: AsymmetricSingleToMany<TO, FROM>
            get() = AsymmetricSingleToMany(
                    name = name)
    }

    data class AsymmetricManyToMany<FROM : Vertex, TO : Vertex>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD
    ) : ManyToMany<FROM, TO> {

        override val inverse: AsymmetricManyToMany<TO, FROM>
            get() = AsymmetricManyToMany(
                    name = name,
                    direction = direction.inverse)
    }

    interface FromOne<FROM : Vertex, TO : Vertex> : Relationship<FROM, TO>, Connection.FromOne<FROM, TO> {

        override val inverse: ToOne<TO, FROM>
    }

    interface FromOptional<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO>, Connection.FromOptional<FROM, TO> {

        override val inverse: ToOptional<TO, FROM>
    }

    interface FromSingle<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO>, Connection.FromSingle<FROM, TO> {

        override val inverse: ToSingle<TO, FROM>
    }

    interface FromMany<FROM : Vertex, TO : Vertex> : Relationship<FROM, TO>, Connection.FromMany<FROM, TO> {

        override val inverse: ToMany<TO, FROM>
    }

    interface ToOne<FROM : Vertex, TO : Vertex> : Relationship<FROM, TO>, Connection.ToOne<FROM, TO> {

        override val inverse: FromOne<TO, FROM>
    }

    interface ToOptional<FROM : Vertex, TO : Vertex> : ToOne<FROM, TO>, Connection.ToOptional<FROM, TO> {

        override val inverse: FromOptional<TO, FROM>
    }

    interface ToSingle<FROM : Vertex, TO : Vertex> : ToOne<FROM, TO>, Connection.ToSingle<FROM, TO> {

        override val inverse: FromSingle<TO, FROM>
    }

    interface ToMany<FROM : Vertex, TO : Vertex> : Relationship<FROM, TO>, Connection.ToMany<FROM, TO> {

        override val inverse: FromMany<TO, FROM>
    }

    interface OneToOne<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO>, ToOne<FROM, TO>, Connection.OneToOne<FROM, TO> {

        override val inverse: OneToOne<TO, FROM>
    }

    interface OneToOptional<FROM : Vertex, TO : Vertex> : OneToOne<FROM, TO>, ToOptional<FROM, TO>, Connection.OneToOptional<FROM, TO> {

        override val inverse: OptionalToOne<TO, FROM>
    }

    interface OneToSingle<FROM : Vertex, TO : Vertex> : OneToOne<FROM, TO>, ToSingle<FROM, TO>, Connection.OneToSingle<FROM, TO> {

        override val inverse: SingleToOne<TO, FROM>
    }

    interface OptionalToOne<FROM : Vertex, TO : Vertex> : FromOptional<FROM, TO>, OneToOne<FROM, TO>, Connection.OptionalToOne<FROM, TO> {

        override val inverse: OneToOptional<TO, FROM>
    }

    interface SingleToOne<FROM : Vertex, TO : Vertex> : FromSingle<FROM, TO>, OneToOne<FROM, TO>, Connection.SingleToOne<FROM, TO> {

        override val inverse: OneToSingle<TO, FROM>
    }

    interface OneToMany<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO>, ToMany<FROM, TO>, Connection.OneToMany<FROM, TO> {

        override val inverse: ManyToOne<TO, FROM>
    }

    interface ManyToOne<FROM : Vertex, TO : Vertex> : FromMany<FROM, TO>, ToOne<FROM, TO>, Connection.ManyToOne<FROM, TO> {

        override val inverse: OneToMany<TO, FROM>
    }

    interface OptionalToOptional<FROM : Vertex, TO : Vertex> : OptionalToOne<FROM, TO>, OneToOptional<FROM, TO>, Connection.OptionalToOptional<FROM, TO> {

        override val inverse: OptionalToOptional<TO, FROM>
    }

    interface OptionalToSingle<FROM : Vertex, TO : Vertex> : OptionalToOne<FROM, TO>, OneToSingle<FROM, TO>, Connection.OptionalToSingle<FROM, TO> {

        override val inverse: SingleToOptional<TO, FROM>
    }

    interface SingleToOptional<FROM : Vertex, TO : Vertex> : SingleToOne<FROM, TO>, OneToOptional<FROM, TO>, Connection.SingleToOptional<FROM, TO> {

        override val inverse: OptionalToSingle<TO, FROM>
    }

    interface SingleToSingle<FROM : Vertex, TO : Vertex> : SingleToOne<FROM, TO>, OneToSingle<FROM, TO>, Connection.SingleToSingle<FROM, TO> {

        override val inverse: SingleToSingle<TO, FROM>
    }

    interface OptionalToMany<FROM : Vertex, TO : Vertex> : FromOptional<FROM, TO>, OneToMany<FROM, TO>, Connection.OptionalToMany<FROM, TO> {

        override val inverse: ManyToOptional<TO, FROM>
    }

    interface SingleToMany<FROM : Vertex, TO : Vertex> : FromSingle<FROM, TO>, OneToMany<FROM, TO>, Connection.SingleToMany<FROM, TO> {

        override val inverse: ManyToSingle<TO, FROM>
    }

    interface ManyToOptional<FROM : Vertex, TO : Vertex> : ManyToOne<FROM, TO>, ToOptional<FROM, TO>, Connection.ManyToOptional<FROM, TO> {

        override val inverse: OptionalToMany<TO, FROM>
    }

    interface ManyToSingle<FROM : Vertex, TO : Vertex> : ManyToOne<FROM, TO>, ToSingle<FROM, TO>, Connection.ManyToSingle<FROM, TO> {

        override val inverse: SingleToMany<TO, FROM>
    }

    interface ManyToMany<FROM : Vertex, TO : Vertex> : FromMany<FROM, TO>, ToMany<FROM, TO>, Connection.ManyToMany<FROM, TO> {

        override val inverse: ManyToMany<TO, FROM>
    }

    interface Symmetric<TYPE : Vertex> : Relationship<TYPE, TYPE> {

        override val direction: Direction? get() = null
    }

    interface AsymmetricManyToOne<FROM : Vertex, TO : Vertex> : ManyToOne<FROM, TO> {

        override val direction: Direction? get() = Direction.BACKWARD
    }

    interface AsymmetricOneToMany<FROM : Vertex, TO : Vertex> : OneToMany<FROM, TO> {

        override val direction: Direction? get() = Direction.FORWARD
    }

    companion object {

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'FROM' object,
         * there will be 0 or 1 'TO' objects. When the [inverse] is traversed from a 'TO' object,
         * there will be 0 or 1 'FROM' objects.
         */
        inline fun <reified FROM : Vertex, reified TO : Vertex> asymmetricOptionalToOptional(
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
        inline fun <reified FROM : Vertex, reified TO : Vertex> asymmetricOptionalToSingle(
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
        inline fun <reified FROM : Vertex, reified TO : Vertex> asymmetricSingleToOptional(
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
        inline fun <reified FROM : Vertex, reified TO : Vertex> asymmetricSingleToSingle(
                name: String,
                direction: Direction = Direction.FORWARD
        ) = AsymmetricSingleToSingle<FROM, TO>(
                name = name,
                direction = direction)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'FROM' object,
         * there will be 0 or more 'TO' objects. When the [inverse] is traversed from a 'TO' object,
         * there will be exactly 1 'FROM' object.
         */
        inline fun <reified FROM : Vertex, reified TO : Vertex> asymmetricSingleToMany(
                name: String
        ) = AsymmetricSingleToMany<FROM, TO>(
                name = name)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'FROM' object,
         * there will be 0 or more 'TO' objects. When the [inverse] is traversed from a 'TO' object,
         * there will be 0 or 1 'FROM' object.
         */
        inline fun <reified FROM : Vertex, reified TO : Vertex> asymmetricOptionalToMany(
                name: String
        ) = AsymmetricOptionalToMany<FROM, TO>(
                name = name)

        /**
         * Creates a [Relationship] that is uni-directional. When traversed from a 'FROM' object,
         * there will be 0 or more 'TO' objects. When the [inverse] is traversed from a 'TO' object,
         * there will be 0 or more 'FROM' objects.
         */
        inline fun <reified FROM : Vertex, reified TO : Vertex> asymmetricManyToMany(
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
        inline fun <reified TYPE : Vertex> symmetricOptionalToOptional(
                name: String
        ) = SymmetricOptionalToOptional<TYPE>(
                name = name)

        /**
         * Creates a [Relationship] that is bi-directional. When traversed from a 'FROM' object,
         * there will be exactly 1 'TO' object. When the [inverse] is traversed from a 'TO' object,
         * there will be exactly 1 'FROM' object.
         */
        inline fun <reified TYPE : Vertex> symmetricSingleToSingle(
                name: String
        ) = SymmetricSingleToSingle<TYPE>(
                name = name)

        /**
         * Creates a [Relationship] that is bi-directional. When traversed from a 'FROM' object,
         * there will be 0 or more 'TO' objects. When the [inverse] is traversed from a 'TO' object,
         * there will be 0 or more 'FROM' objects.
         */
        inline fun <reified TYPE : Vertex> symmetricManyToMany(
                name: String
        ) = SymmetricManyToMany<TYPE>(
                name = name)
    }
}
