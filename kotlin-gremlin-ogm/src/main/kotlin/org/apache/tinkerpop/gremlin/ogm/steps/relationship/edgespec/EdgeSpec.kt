package org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec

import org.apache.tinkerpop.gremlin.ogm.GraphVertex
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.MissingEdge
import org.apache.tinkerpop.gremlin.ogm.steps.bound.edgespec.BoundEdgeSpec
import org.apache.tinkerpop.gremlin.ogm.steps.bound.edgespec.BoundEdgeSpecToMany
import org.apache.tinkerpop.gremlin.ogm.steps.bound.edgespec.BoundEdgeSpecToOptional
import org.apache.tinkerpop.gremlin.ogm.steps.bound.edgespec.BoundEdgeSpecToSingle
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.edgespec.SingleBoundEdgeSpec
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.edgespec.SingleBoundEdgeSpecToMany
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.edgespec.SingleBoundEdgeSpecToOptional
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.edgespec.SingleBoundEdgeSpecToSingle
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * An [EdgeSpec] defines a connection between two vertices that does not travel through any other vertices.
 * Each [EdgeSpec] must be registered with a GraphMapper.
 */
interface EdgeSpec<FROM : Vertex, TO : Vertex> : Relationship<FROM, TO> {

    /**
     * Relationships may be asymmetric, meaning if vertex A relates to vertex B, that
     * does not imply B relates to A. Thus, a spec name and two vertices is not
     * enough to represent the full semantics of a [EdgeSpec], we also need [Direction]
     * to specify the origin and target of an asymmetric spec.
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
     * The [Direction] of the [EdgeSpec], or null if the [EdgeSpec] is [Symmetric]
     */
    val direction: Direction?

    /**
     * The name of the [EdgeSpec] that will be stored to the graph. [EdgeSpec.name]s
     * must be globally unique.
     */
    val name: String

    override val inverse: Relationship<TO, FROM>

    override fun traverse(traversal: GraphTraversal<*, GraphVertex>): GraphTraversal<*, GraphVertex> =
            when (direction) {
                Direction.FORWARD -> traversal.out(name)
                Direction.BACKWARD -> traversal.`in`(name)
                null -> traversal.both(name)
            }

    interface FromOne<FROM : Vertex, TO : Vertex> : EdgeSpec<FROM, TO>, Relationship.FromOne<FROM, TO> {

        override val inverse: ToOne<TO, FROM>
    }

    interface FromOptional<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO>, Relationship.FromOptional<FROM, TO> {

        override val inverse: ToOptional<TO, FROM>
    }

    interface FromSingle<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO>, Relationship.FromSingle<FROM, TO> {

        override val inverse: ToSingle<TO, FROM>
    }

    interface FromMany<FROM : Vertex, TO : Vertex> : EdgeSpec<FROM, TO>, Relationship.FromMany<FROM, TO> {

        override val inverse: ToMany<TO, FROM>
    }

    interface ToOne<FROM : Vertex, TO : Vertex> : EdgeSpec<FROM, TO>, Relationship.ToOne<FROM, TO> {

        override val inverse: FromOne<TO, FROM>
    }

    interface ToOptional<FROM : Vertex, TO : Vertex> : ToOne<FROM, TO>, Relationship.ToOptional<FROM, TO> {

        override val inverse: FromOptional<TO, FROM>

        override infix fun from(from: FROM): SingleBoundEdgeSpec.ToOptional<FROM, TO> = SingleBoundEdgeSpecToOptional(from, this)
        override infix fun from(froms: Collection<FROM>): BoundEdgeSpec.ToOptional<FROM, TO> = BoundEdgeSpecToOptional(froms.toList(), this)
        override fun from(vararg froms: FROM): BoundEdgeSpec.ToOptional<FROM, TO> = BoundEdgeSpecToOptional(froms.toList(), this)

    }

    interface ToSingle<FROM : Vertex, TO : Vertex> : ToOne<FROM, TO>, Relationship.ToSingle<FROM, TO> {

        override val inverse: FromSingle<TO, FROM>

        override infix fun from(from: FROM): SingleBoundEdgeSpec.ToSingle<FROM, TO> = SingleBoundEdgeSpecToSingle(from, this)
        override infix fun from(froms: Collection<FROM>): BoundEdgeSpec.ToSingle<FROM, TO> = BoundEdgeSpecToSingle(froms.toList(), this)
        override fun from(vararg froms: FROM): BoundEdgeSpec.ToSingle<FROM, TO> = BoundEdgeSpecToSingle(froms.toList(), this)

        override fun traverse(traversal: GraphTraversal<*, GraphVertex>): GraphTraversal<*, GraphVertex> {
            fun throwMissingEdge(): GraphTraversal<*, GraphVertex> =
                    DefaultGraphTraversal<Vertex, GraphVertex>().map { throw MissingEdge(it.get(), name) }
            return traversal.coalesce(DefaultGraphTraversal<Vertex, GraphVertex>().run {
                when (direction) {
                    EdgeSpec.Direction.FORWARD -> out(name)
                    EdgeSpec.Direction.BACKWARD -> `in`(name)
                    null -> both(name)
                }
            }, throwMissingEdge())
        }
    }

    interface ToMany<FROM : Vertex, TO : Vertex> : EdgeSpec<FROM, TO>, Relationship.ToMany<FROM, TO> {

        override val inverse: FromMany<TO, FROM>

        override infix fun from(from: FROM): SingleBoundEdgeSpec.ToMany<FROM, TO> = SingleBoundEdgeSpecToMany(from, this)
        override infix fun from(froms: Collection<FROM>): BoundEdgeSpec.ToMany<FROM, TO> = BoundEdgeSpecToMany(froms.toList(), this)
        override fun from(vararg froms: FROM): BoundEdgeSpec.ToMany<FROM, TO> = BoundEdgeSpecToMany(froms.toList(), this)
    }

    interface Asymmetric<FROM : Vertex, TO : Vertex> : EdgeSpec<FROM, TO> {

        override val direction: Direction

        override val inverse: Asymmetric<TO, FROM>
    }

    interface Symmetric<TYPE : Vertex> : EdgeSpec<TYPE, TYPE> {

        override val direction: Direction? get() = null

        override val inverse: Symmetric<TYPE> get() = this
    }

    interface OneToOne<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO>, ToOne<FROM, TO>, Relationship.OneToOne<FROM, TO> {

        override val inverse: OneToOne<TO, FROM>

        interface Asymmetric<FROM : Vertex, TO : Vertex> : OneToOne<FROM, TO>, Relationship.OneToOne<FROM, TO>, EdgeSpec.Asymmetric<FROM, TO> {

            override val inverse: Asymmetric<TO, FROM>
        }

        interface Symmetric<TYPE : Vertex> : OneToOne<TYPE, TYPE>, Relationship.OneToOne<TYPE, TYPE>, EdgeSpec.Symmetric<TYPE> {

            override val inverse: Symmetric<TYPE> get() = this
        }
    }

    interface OneToOptional<FROM : Vertex, TO : Vertex> : OneToOne<FROM, TO>, ToOptional<FROM, TO>, Relationship.OneToOptional<FROM, TO> {

        override val inverse: OptionalToOne<TO, FROM>
    }

    interface OneToSingle<FROM : Vertex, TO : Vertex> : OneToOne<FROM, TO>, ToSingle<FROM, TO>, Relationship.OneToSingle<FROM, TO> {

        override val inverse: SingleToOne<TO, FROM>
    }

    interface OptionalToOne<FROM : Vertex, TO : Vertex> : FromOptional<FROM, TO>, OneToOne<FROM, TO>, Relationship.OptionalToOne<FROM, TO> {

        override val inverse: OneToOptional<TO, FROM>
    }

    interface SingleToOne<FROM : Vertex, TO : Vertex> : FromSingle<FROM, TO>, OneToOne<FROM, TO>, Relationship.SingleToOne<FROM, TO> {

        override val inverse: OneToSingle<TO, FROM>
    }

    interface OneToMany<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO>, ToMany<FROM, TO>, Relationship.OneToMany<FROM, TO>, Asymmetric<FROM, TO> {

        override val inverse: ManyToOne<TO, FROM>

        override val direction get() = Direction.FORWARD
    }

    interface ManyToOne<FROM : Vertex, TO : Vertex> : FromMany<FROM, TO>, ToOne<FROM, TO>, Relationship.ManyToOne<FROM, TO>, Asymmetric<FROM, TO> {

        override val inverse: OneToMany<TO, FROM>

        override val direction get() = Direction.BACKWARD
    }

    interface OptionalToOptional<FROM : Vertex, TO : Vertex> : OptionalToOne<FROM, TO>, OneToOptional<FROM, TO>, Relationship.OptionalToOptional<FROM, TO> {

        override val inverse: OptionalToOptional<TO, FROM>

        interface Asymmetric<FROM : Vertex, TO : Vertex> : OptionalToOptional<FROM, TO>, OneToOne.Asymmetric<FROM, TO> {

            override val inverse: Asymmetric<TO, FROM> get() = OptionalToOptionalAsymmetricEdgeSpec(name, direction.inverse)
        }

        interface Symmetric<TYPE : Vertex> : OptionalToOptional<TYPE, TYPE>, OneToOne.Symmetric<TYPE> {

            override val inverse: Symmetric<TYPE> get() = this
        }
    }

    interface OptionalToSingle<FROM : Vertex, TO : Vertex> : OptionalToOne<FROM, TO>, OneToSingle<FROM, TO>, Relationship.OptionalToSingle<FROM, TO>, Asymmetric<FROM, TO> {

        override val inverse: SingleToOptional<TO, FROM> get() = SingleToOptionalEdgeSpec(name = name, direction = direction.inverse)
    }

    interface SingleToOptional<FROM : Vertex, TO : Vertex> : SingleToOne<FROM, TO>, OneToOptional<FROM, TO>, Relationship.SingleToOptional<FROM, TO>, Asymmetric<FROM, TO> {

        override val inverse: OptionalToSingle<TO, FROM> get() = OptionalToSingleEdgeSpec(name = name, direction = direction.inverse)
    }

    interface SingleToSingle<FROM : Vertex, TO : Vertex> : SingleToOne<FROM, TO>, OneToSingle<FROM, TO>, Relationship.SingleToSingle<FROM, TO> {

        override val inverse: SingleToSingle<TO, FROM>

        interface Asymmetric<FROM : Vertex, TO : Vertex> : SingleToSingle<FROM, TO>, OneToOne.Asymmetric<FROM, TO> {

            override val inverse: Asymmetric<TO, FROM> get() = SingleToSingleAsymmetricEdgeSpec(name = name, direction = direction.inverse)
        }

        interface Symmetric<TYPE : Vertex> : SingleToSingle<TYPE, TYPE>, OneToOne.Symmetric<TYPE> {

            override val inverse: Symmetric<TYPE> get() = this
        }
    }

    interface OptionalToMany<FROM : Vertex, TO : Vertex> : FromOptional<FROM, TO>, OneToMany<FROM, TO>, Relationship.OptionalToMany<FROM, TO> {

        override val inverse: ManyToOptional<TO, FROM> get() = ManyToOptionalEdgeSpec(name = name)
    }

    interface SingleToMany<FROM : Vertex, TO : Vertex> : FromSingle<FROM, TO>, OneToMany<FROM, TO>, Relationship.SingleToMany<FROM, TO> {

        override val inverse: ManyToSingle<TO, FROM> get() = ManyToSingleEdgeSpec(name = name)
    }

    interface ManyToOptional<FROM : Vertex, TO : Vertex> : ManyToOne<FROM, TO>, ToOptional<FROM, TO>, Relationship.ManyToOptional<FROM, TO> {

        override val inverse: OptionalToMany<TO, FROM> get() = OptionalToManyEdgeSpec(name = name)
    }

    interface ManyToSingle<FROM : Vertex, TO : Vertex> : ManyToOne<FROM, TO>, ToSingle<FROM, TO>, Relationship.ManyToSingle<FROM, TO> {

        override val inverse: SingleToMany<TO, FROM> get() = SingleToManyEdgeSpec(name = name)
    }

    interface ManyToMany<FROM : Vertex, TO : Vertex> : FromMany<FROM, TO>, ToMany<FROM, TO>, Relationship.ManyToMany<FROM, TO> {

        override val inverse: ManyToMany<TO, FROM>

        interface Asymmetric<FROM : Vertex, TO : Vertex> : ManyToMany<FROM, TO>, EdgeSpec.Asymmetric<FROM, TO> {

            override val inverse: Asymmetric<TO, FROM> get() = ManyToManyAsymmetricEdgeSpec(name = name, direction = direction.inverse)
        }

        interface Symmetric<TYPE : Vertex> : ManyToMany<TYPE, TYPE>, EdgeSpec.Symmetric<TYPE> {

            override val inverse: Symmetric<TYPE> get() = this
        }
    }
}
