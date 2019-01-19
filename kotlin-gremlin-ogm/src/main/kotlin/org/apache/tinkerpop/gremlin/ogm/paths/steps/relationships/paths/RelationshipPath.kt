package org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.paths

import org.apache.tinkerpop.gremlin.ogm.GraphVertex
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.StepTraverser
import org.apache.tinkerpop.gremlin.ogm.paths.steps.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.Relationship
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal


/**
 * A path through the graph starting and ending at a vertex, which spans two or more edges.
 * The special property of a [RelationshipPath] (opposed to a normal [Path]) is that it can be reversed.
 */
internal interface RelationshipPath<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : Path<FROM, MIDDLE, TO>, Relationship<FROM, TO> {

    override val first: Relationship<FROM, MIDDLE>

    override val last: Relationship<MIDDLE, TO>

    override val inverse: RelationshipPath<TO, MIDDLE, FROM>

    override fun traverse(traversal: GraphTraversal<*, GraphVertex>): GraphTraversal<*, GraphVertex> {
        val firstTraversal = first.traverse(traversal)
        val lastTraversal = last.traverse(firstTraversal)
        return lastTraversal
    }

    override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, TO> = super<Relationship>.invoke(from)

    interface FromOne<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : RelationshipPath<FROM, MIDDLE, TO>, Relationship.FromOne<FROM, TO> {

        override val first: Relationship.FromOne<FROM, MIDDLE>
        override val last: Relationship.FromOne<MIDDLE, TO>
        override val inverse: ToOne<TO, MIDDLE, FROM>
    }

    interface FromOptional<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : FromOne<FROM, MIDDLE, TO>, Relationship.FromOptional<FROM, TO> {

        override val inverse: ToOptional<TO, MIDDLE, FROM>
    }

    interface FromSingle<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : FromOne<FROM, MIDDLE, TO>, Relationship.FromSingle<FROM, TO> {

        override val first: Relationship.FromSingle<FROM, MIDDLE>
        override val last: Relationship.FromSingle<MIDDLE, TO>
        override val inverse: ToSingle<TO, MIDDLE, FROM>
    }

    interface FromMany<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : RelationshipPath<FROM, MIDDLE, TO>, Relationship.FromMany<FROM, TO> {

        override val inverse: ToMany<TO, MIDDLE, FROM>
    }

    interface ToOne<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : RelationshipPath<FROM, MIDDLE, TO>, Path.ToOne<FROM, MIDDLE, TO>, Relationship.ToOne<FROM, TO> {

        override val first: Relationship.ToOne<FROM, MIDDLE>
        override val last: Relationship.ToOne<MIDDLE, TO>
        override val inverse: FromOne<TO, MIDDLE, FROM>
    }

    interface ToOptional<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : ToOne<FROM, MIDDLE, TO>, Path.ToOptional<FROM, MIDDLE, TO>, Relationship.ToOptional<FROM, TO> {

        override val inverse: FromOptional<TO, MIDDLE, FROM>
    }

    interface ToSingle<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : ToOne<FROM, MIDDLE, TO>, Path.ToSingle<FROM, MIDDLE, TO>, Relationship.ToSingle<FROM, TO> {

        override val first: Relationship.ToSingle<FROM, MIDDLE>
        override val last: Relationship.ToSingle<MIDDLE, TO>
        override val inverse: FromSingle<TO, MIDDLE, FROM>
    }

    interface ToMany<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : RelationshipPath<FROM, MIDDLE, TO>, Path.ToMany<FROM, MIDDLE, TO>, Relationship.ToMany<FROM, TO> {

        override val inverse: FromMany<TO, MIDDLE, FROM>
    }

    interface OneToOne<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : FromOne<FROM, MIDDLE, TO>, ToOne<FROM, MIDDLE, TO>, Relationship.OneToOne<FROM, TO> {

        override val first: Relationship.OneToOne<FROM, MIDDLE>
        override val last: Relationship.OneToOne<MIDDLE, TO>
        override val inverse: OneToOne<TO, MIDDLE, FROM>
    }

    interface OneToOptional<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : OneToOne<FROM, MIDDLE, TO>, ToOptional<FROM, MIDDLE, TO>, Relationship.OneToOptional<FROM, TO> {

        override val inverse: OptionalToOne<TO, MIDDLE, FROM>
    }

    interface OneToSingle<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : OneToOne<FROM, MIDDLE, TO>, ToSingle<FROM, MIDDLE, TO>, Relationship.OneToSingle<FROM, TO> {

        override val first: Relationship.OneToSingle<FROM, MIDDLE>
        override val last: Relationship.OneToSingle<MIDDLE, TO>
        override val inverse: SingleToOne<TO, MIDDLE, FROM>
    }

    interface OptionalToOne<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : FromOptional<FROM, MIDDLE, TO>, OneToOne<FROM, MIDDLE, TO>, Relationship.OptionalToOne<FROM, TO> {

        override val inverse: OneToOptional<TO, MIDDLE, FROM>
    }

    interface SingleToOne<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : FromSingle<FROM, MIDDLE, TO>, OneToOne<FROM, MIDDLE, TO>, Relationship.SingleToOne<FROM, TO> {

        override val first: Relationship.SingleToOne<FROM, MIDDLE>
        override val last: Relationship.SingleToOne<MIDDLE, TO>
        override val inverse: OneToSingle<TO, MIDDLE, FROM>
    }

    interface OneToMany<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : FromOne<FROM, MIDDLE, TO>, ToMany<FROM, MIDDLE, TO>, Relationship.OneToMany<FROM, TO> {

        override val inverse: ManyToOne<TO, MIDDLE, FROM>
    }

    interface ManyToOne<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : FromMany<FROM, MIDDLE, TO>, ToOne<FROM, MIDDLE, TO>, Relationship.ManyToOne<FROM, TO> {

        override val inverse: OneToMany<TO, MIDDLE, FROM>
    }

    interface OptionalToOptional<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : OptionalToOne<FROM, MIDDLE, TO>, OneToOptional<FROM, MIDDLE, TO>, Relationship.OptionalToOptional<FROM, TO> {

        override val inverse: OptionalToOptional<TO, MIDDLE, FROM> get() = OptionalToOptionalRelationshipPath(last.inverse, first.inverse)
    }

    interface OptionalToSingle<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : OptionalToOne<FROM, MIDDLE, TO>, OneToSingle<FROM, MIDDLE, TO>, Relationship.OptionalToSingle<FROM, TO> {

        override val first: Relationship.OneToSingle<FROM, MIDDLE>
        override val last: Relationship.OneToSingle<MIDDLE, TO>
        override val inverse: SingleToOptional<TO, MIDDLE, FROM> get() = SingleToOptionalRelationshipPath(last.inverse, first.inverse)
    }

    interface SingleToOptional<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : SingleToOne<FROM, MIDDLE, TO>, OneToOptional<FROM, MIDDLE, TO>, Relationship.SingleToOptional<FROM, TO> {

        override val first: Relationship.SingleToOne<FROM, MIDDLE>
        override val last: Relationship.SingleToOne<MIDDLE, TO>
        override val inverse: OptionalToSingle<TO, MIDDLE, FROM> get() = OptionalToSingleRelationshipPath(last.inverse, first.inverse)
    }

    interface SingleToSingle<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : SingleToOne<FROM, MIDDLE, TO>, OneToSingle<FROM, MIDDLE, TO>, Relationship.SingleToSingle<FROM, TO> {

        override val first: Relationship.SingleToSingle<FROM, MIDDLE>
        override val last: Relationship.SingleToSingle<MIDDLE, TO>
        override val inverse: SingleToSingle<TO, MIDDLE, FROM> get() = SingleToSingleRelationshipPath(last.inverse, first.inverse)
    }

    interface OptionalToMany<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : FromOptional<FROM, MIDDLE, TO>, OneToMany<FROM, MIDDLE, TO>, Relationship.OptionalToMany<FROM, TO> {

        override val inverse: ManyToOptional<TO, MIDDLE, FROM> get() = ManyToOptionalRelationshipPath(last.inverse, first.inverse)
    }

    interface SingleToMany<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : FromSingle<FROM, MIDDLE, TO>, OneToMany<FROM, MIDDLE, TO>, Relationship.SingleToMany<FROM, TO> {

        override val inverse: ManyToSingle<TO, MIDDLE, FROM> get() = ManyToSingleRelationshipPath(last.inverse, first.inverse)
    }

    interface ManyToOptional<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : ManyToOne<FROM, MIDDLE, TO>, ToOptional<FROM, MIDDLE, TO>, Relationship.ManyToOptional<FROM, TO> {

        override val inverse: OptionalToMany<TO, MIDDLE, FROM> get() = OptionalToManyRelationshipPath(last.inverse, first.inverse)
    }

    interface ManyToSingle<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : ManyToOne<FROM, MIDDLE, TO>, ToSingle<FROM, MIDDLE, TO>, Relationship.ManyToSingle<FROM, TO> {

        override val inverse: SingleToMany<TO, MIDDLE, FROM> get() = SingleToManyRelationshipPath(last.inverse, first.inverse)
    }

    interface ManyToMany<FROM : Vertex, MIDDLE : Vertex, TO : Vertex> : FromMany<FROM, MIDDLE, TO>, ToMany<FROM, MIDDLE, TO>, Relationship.ManyToMany<FROM, TO> {

        override val inverse: ManyToMany<TO, MIDDLE, FROM> get() = ManyToManyRelationshipPath(last.inverse, first.inverse)
    }
}
