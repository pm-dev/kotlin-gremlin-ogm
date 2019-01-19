package org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships

import org.apache.tinkerpop.gremlin.ogm.GraphVertex
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.MissingEdge
import org.apache.tinkerpop.gremlin.ogm.exceptions.ObjectNotSaved
import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step
import org.apache.tinkerpop.gremlin.ogm.paths.steps.StepTraverser
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.paths.*
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A [Relationship] defines a path between two vertices that may or may not traverse through other vertices.
 */
interface Relationship<FROM : Vertex, TO : Vertex> : Step<FROM, TO> {

    /**
     * This spec in the opposite direction
     */
    val inverse: Relationship<TO, FROM>

    fun traverse(traversal: GraphTraversal<*, GraphVertex>): GraphTraversal<*, GraphVertex>

    fun serializeFroms(stepper: StepTraverser<FROM>): GraphTraversal<*, GraphVertex> =
            stepper.traversal.map { fromTraverser ->
                val mapper = stepper.graphMapper
                val deserializedFrom = fromTraverser.get()
                val id = mapper.vertexID(deserializedFrom) ?: throw ObjectNotSaved(deserializedFrom)
                mapper.g.V(id).next()
            }

    fun deserializeTos(stepper: StepTraverser<GraphVertex>): GraphTraversal<*, TO> =
            stepper.traversal.map { toTraverser -> stepper.graphMapper.deserialize<TO>(toTraverser.get()) }

    override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, TO> {
        val serialized = serializeFroms(from)
        val traversed = traverse(serialized)
        val newStepper = StepTraverser(traversed, from.graphMapper)
        return deserializeTos(newStepper)
    }

    infix fun <NEXT : Vertex> link(next: ManyToMany<TO, NEXT>): ManyToMany<FROM, NEXT> = ManyToManyRelationshipPath(first = this, last = next)

    interface FromOne<FROM : Vertex, TO : Vertex> : Relationship<FROM, TO> {

        override val inverse: ToOne<TO, FROM>

        infix fun <NEXT : Vertex> link(next: OptionalToMany<TO, NEXT>): OptionalToMany<FROM, NEXT> = OptionalToManyRelationshipPath(first = this, last = next)
    }

    interface FromOptional<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO> {

        override val inverse: ToOptional<TO, FROM>

        infix fun <NEXT : Vertex> link(next: SingleToMany<TO, NEXT>): OptionalToMany<FROM, NEXT> = OptionalToManyRelationshipPath(first = this, last = next)
    }

    interface FromSingle<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO> {

        override val inverse: ToSingle<TO, FROM>

        infix fun <NEXT : Vertex> link(next: Relationship.SingleToMany<TO, NEXT>): SingleToMany<FROM, NEXT> = SingleToManyRelationshipPath(first = this, last = next)
    }

    interface FromMany<FROM : Vertex, TO : Vertex> : Relationship<FROM, TO> {

        override val inverse: ToMany<TO, FROM>

        infix fun <NEXT : Vertex> link(next: ToMany<TO, NEXT>): ManyToMany<FROM, NEXT> = ManyToManyRelationshipPath(first = this, last = next)
    }

    interface ToOne<FROM : Vertex, TO : Vertex> : Relationship<FROM, TO>, Step.ToOne<FROM, TO> {

        override val inverse: FromOne<TO, FROM>

        infix fun <NEXT : Vertex> link(next: ManyToOptional<TO, NEXT>): ManyToOptional<FROM, NEXT> = ManyToOptionalRelationshipPath(first = this, last = next)
    }

    interface ToOptional<FROM : Vertex, TO : Vertex> : ToOne<FROM, TO>, Step.ToOptional<FROM, TO> {

        override val inverse: FromOptional<TO, FROM>
    }

    interface ToSingle<FROM : Vertex, TO : Vertex> : ToOne<FROM, TO>, Step.ToSingle<FROM, TO> {

        override val inverse: FromSingle<TO, FROM>

        infix fun <NEXT : Vertex> link(next: ManyToSingle<TO, NEXT>): ManyToSingle<FROM, NEXT> = ManyToSingleRelationshipPath(first = this, last = next)
    }

    interface ToMany<FROM : Vertex, TO : Vertex> : Relationship<FROM, TO>, Step.ToMany<FROM, TO> {

        override val inverse: FromMany<TO, FROM>
    }

    interface OneToOne<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO>, ToOne<FROM, TO> {

        override val inverse: OneToOne<TO, FROM>
    }

    interface OneToOptional<FROM : Vertex, TO : Vertex> : OneToOne<FROM, TO>, ToOptional<FROM, TO> {

        override val inverse: OptionalToOne<TO, FROM>
    }

    interface OneToSingle<FROM : Vertex, TO : Vertex> : OneToOne<FROM, TO>, ToSingle<FROM, TO> {

        override val inverse: SingleToOne<TO, FROM>

        infix fun <NEXT : Vertex> link(next: OptionalToSingle<TO, NEXT>): OptionalToSingle<FROM, NEXT> = OptionalToSingleRelationshipPath(first = this, last = next)
    }

    interface OptionalToOne<FROM : Vertex, TO : Vertex> : FromOptional<FROM, TO>, OneToOne<FROM, TO> {

        override val inverse: OneToOptional<TO, FROM>

        infix fun <NEXT : Vertex> link(next: OneToOptional<TO, NEXT>): OptionalToOptional<FROM, NEXT> = OptionalToOptionalRelationshipPath(first = this, last = next)
    }

    interface SingleToOne<FROM : Vertex, TO : Vertex> : FromSingle<FROM, TO>, OneToOne<FROM, TO> {

        override val inverse: OneToSingle<TO, FROM>

        infix fun <NEXT : Vertex> link(next: OptionalToOptional<TO, NEXT>): OptionalToOptional<FROM, NEXT> = OptionalToOptionalRelationshipPath(first = this, last = next)
    }

    interface OneToMany<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO>, ToMany<FROM, TO> {

        override val inverse: ManyToOne<TO, FROM>
    }

    interface ManyToOne<FROM : Vertex, TO : Vertex> : FromMany<FROM, TO>, ToOne<FROM, TO> {

        override val inverse: OneToMany<TO, FROM>
    }

    interface OptionalToOptional<FROM : Vertex, TO : Vertex> : OptionalToOne<FROM, TO>, OneToOptional<FROM, TO> {

        override val inverse: OptionalToOptional<TO, FROM>

        infix fun <NEXT : Vertex> link(next: OneToSingle<TO, NEXT>): OptionalToOptional<FROM, NEXT> = OptionalToOptionalRelationshipPath(first = this, last = next)
    }

    interface OptionalToSingle<FROM : Vertex, TO : Vertex> : OptionalToOne<FROM, TO>, OneToSingle<FROM, TO> {

        override val inverse: SingleToOptional<TO, FROM>

        infix fun <NEXT : Vertex> link(next: OneToSingle<TO, NEXT>): OptionalToSingle<FROM, NEXT> = OptionalToSingleRelationshipPath(first = this, last = next)
    }

    interface SingleToOptional<FROM : Vertex, TO : Vertex> : SingleToOne<FROM, TO>, OneToOptional<FROM, TO> {

        override val inverse: OptionalToSingle<TO, FROM>

        infix fun <NEXT : Vertex> link(next: SingleToOne<TO, NEXT>): SingleToOptional<FROM, NEXT> = SingleToOptionalRelationshipPath(first = this, last = next)
    }

    interface SingleToSingle<FROM : Vertex, TO : Vertex> : SingleToOne<FROM, TO>, OneToSingle<FROM, TO> {

        override val inverse: SingleToSingle<TO, FROM>

        infix fun <NEXT : Vertex> link(next: SingleToOptional<TO, NEXT>): SingleToOptional<FROM, NEXT> = SingleToOptionalRelationshipPath(first = this, last = next)

        infix fun <NEXT : Vertex> link(next: SingleToSingle<TO, NEXT>): SingleToSingle<FROM, NEXT> = SingleToSingleRelationshipPath(first = this, last = next)
    }

    interface OptionalToMany<FROM : Vertex, TO : Vertex> : FromOptional<FROM, TO>, OneToMany<FROM, TO> {

        override val inverse: ManyToOptional<TO, FROM>

        infix fun <NEXT : Vertex> link(next: FromOne<TO, NEXT>): OptionalToMany<FROM, NEXT> = OptionalToManyRelationshipPath(first = this, last = next)
    }

    interface SingleToMany<FROM : Vertex, TO : Vertex> : FromSingle<FROM, TO>, OneToMany<FROM, TO> {

        override val inverse: ManyToSingle<TO, FROM>

        infix fun <NEXT : Vertex> link(next: FromSingle<TO, NEXT>): SingleToMany<FROM, NEXT> = SingleToManyRelationshipPath(first = this, last = next)
    }

    interface ManyToOptional<FROM : Vertex, TO : Vertex> : ManyToOne<FROM, TO>, ToOptional<FROM, TO> {

        override val inverse: OptionalToMany<TO, FROM>

        infix fun <NEXT : Vertex> link(next: ToOne<TO, NEXT>): ManyToOptional<FROM, NEXT> = ManyToOptionalRelationshipPath(first = this, last = next)
    }

    interface ManyToSingle<FROM : Vertex, TO : Vertex> : ManyToOne<FROM, TO>, ToSingle<FROM, TO> {

        override val inverse: SingleToMany<TO, FROM>

        infix fun <NEXT : Vertex> link(next: ToOptional<TO, NEXT>): ManyToOptional<FROM, NEXT> = ManyToOptionalRelationshipPath(first = this, last = next)
        infix fun <NEXT : Vertex> link(next: ToSingle<TO, NEXT>): ManyToSingle<FROM, NEXT> = ManyToSingleRelationshipPath(first = this, last = next)
    }

    interface ManyToMany<FROM : Vertex, TO : Vertex> : FromMany<FROM, TO>, ToMany<FROM, TO> {

        override val inverse: ManyToMany<TO, FROM>

        infix fun <NEXT : Vertex> link(next: Relationship<TO, NEXT>): ManyToMany<FROM, NEXT> = ManyToManyRelationshipPath(first = this, last = next)
    }
}
