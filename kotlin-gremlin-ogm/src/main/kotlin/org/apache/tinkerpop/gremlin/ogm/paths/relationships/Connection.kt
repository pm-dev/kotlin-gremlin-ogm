@file:Suppress("UNCHECKED_CAST")

package org.apache.tinkerpop.gremlin.ogm.paths.relationships

import org.apache.tinkerpop.gremlin.ogm.GraphVertex
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.MissingEdge
import org.apache.tinkerpop.gremlin.ogm.exceptions.ObjectNotSaved
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.steps.StepTraverser
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal


/**
 * A path through the graph starting and ending at a vertex, which spans one or more relationships.
 * A special property of Connections is that they can be reversed.
 */
interface Connection<FROM : Vertex, TO : Vertex> : Path<FROM, TO> {

    /**
     * This Connection to reverse.
     */
    val inverse: Connection<TO, FROM>

    /**
     * The individual [Relationship]s that create this connection
     */
    fun relationships(): List<Relationship<*, *>>

    override fun path() = listOf(this)

    override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, TO> {
        val serialized = from.traversal.map { fromObject ->
            val id = from.graphMapper.vertexID(fromObject.get()) ?: throw ObjectNotSaved(fromObject.get())
            from.graphMapper.traversal.V(id).next()
        }
        val traversed = relationships().fold(initial = serialized) { traversal, relationship ->
            if (relationship is Relationship.ToSingle) {
                traversal.coalesce(traversalTo(relationship), throwMissingEdge(relationship))
            } else {
                traversal.to(relationship)
            }
        }
        return traversed.map { toVertex -> from.graphMapper.deserialize<TO>(toVertex.get()) }
    }

    interface FromOne<FROM : Vertex, TO : Vertex> : Connection<FROM, TO> {

        override val inverse: ToOne<TO, FROM>
    }

    interface FromOptional<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO> {

        override val inverse: ToOptional<TO, FROM>
    }

    interface FromSingle<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO> {

        override val inverse: ToSingle<TO, FROM>
    }

    interface FromMany<FROM : Vertex, TO : Vertex> : Connection<FROM, TO> {

        override val inverse: ToMany<TO, FROM>
    }

    interface ToOne<FROM : Vertex, TO : Vertex> : Connection<FROM, TO>, Path.ToOne<FROM, TO> {

        override val inverse: FromOne<TO, FROM>
    }

    interface ToOptional<FROM : Vertex, TO : Vertex> : ToOne<FROM, TO>, Path.ToOptional<FROM, TO> {

        override val inverse: FromOptional<TO, FROM>
    }

    interface ToSingle<FROM : Vertex, TO : Vertex> : ToOne<FROM, TO>, Path.ToSingle<FROM, TO> {

        override val inverse: FromSingle<TO, FROM>
    }

    interface ToMany<FROM : Vertex, TO : Vertex> : Connection<FROM, TO>, Path.ToMany<FROM, TO> {

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
    }

    interface OptionalToOne<FROM : Vertex, TO : Vertex> : FromOptional<FROM, TO>, OneToOne<FROM, TO> {

        override val inverse: OneToOptional<TO, FROM>
    }

    interface SingleToOne<FROM : Vertex, TO : Vertex> : FromSingle<FROM, TO>, OneToOne<FROM, TO> {

        override val inverse: OneToSingle<TO, FROM>
    }

    interface OneToMany<FROM : Vertex, TO : Vertex> : FromOne<FROM, TO>, ToMany<FROM, TO> {

        override val inverse: ManyToOne<TO, FROM>
    }

    interface ManyToOne<FROM : Vertex, TO : Vertex> : FromMany<FROM, TO>, ToOne<FROM, TO> {

        override val inverse: OneToMany<TO, FROM>
    }

    interface OptionalToOptional<FROM : Vertex, TO : Vertex> : OptionalToOne<FROM, TO>, OneToOptional<FROM, TO> {

        override val inverse: OptionalToOptional<TO, FROM>
    }

    interface OptionalToSingle<FROM : Vertex, TO : Vertex> : OptionalToOne<FROM, TO>, OneToSingle<FROM, TO> {

        override val inverse: SingleToOptional<TO, FROM>
    }

    interface SingleToOptional<FROM : Vertex, TO : Vertex> : SingleToOne<FROM, TO>, OneToOptional<FROM, TO> {

        override val inverse: OptionalToSingle<TO, FROM>
    }

    interface SingleToSingle<FROM : Vertex, TO : Vertex> : SingleToOne<FROM, TO>, OneToSingle<FROM, TO> {

        override val inverse: SingleToSingle<TO, FROM>
    }

    interface OptionalToMany<FROM : Vertex, TO : Vertex> : FromOptional<FROM, TO>, OneToMany<FROM, TO> {

        override val inverse: ManyToOptional<TO, FROM>
    }

    interface SingleToMany<FROM : Vertex, TO : Vertex> : FromSingle<FROM, TO>, OneToMany<FROM, TO> {

        override val inverse: ManyToSingle<TO, FROM>
    }

    interface ManyToOptional<FROM : Vertex, TO : Vertex> : ManyToOne<FROM, TO>, ToOptional<FROM, TO> {

        override val inverse: OptionalToMany<TO, FROM>
    }

    interface ManyToSingle<FROM : Vertex, TO : Vertex> : ManyToOne<FROM, TO>, ToSingle<FROM, TO> {

        override val inverse: SingleToMany<TO, FROM>
    }

    interface ManyToMany<FROM : Vertex, TO : Vertex> : FromMany<FROM, TO>, ToMany<FROM, TO> {

        override val inverse: ManyToMany<TO, FROM>
    }

    companion object {

        private fun throwMissingEdge(relationship: Relationship<*, *>): GraphTraversal<*, GraphVertex> =
                DefaultGraphTraversal<Vertex, org.apache.tinkerpop.gremlin.structure.Vertex>().map {
                    throw MissingEdge(it.get(), relationship.name)
                } as GraphTraversal<Vertex, org.apache.tinkerpop.gremlin.structure.Vertex>

        private fun traversalTo(relationship: Relationship<*, *>): GraphTraversal<*, GraphVertex> =
                DefaultGraphTraversal<Vertex, org.apache.tinkerpop.gremlin.structure.Vertex>().to(relationship)

        private fun GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Vertex>.to(relationship: Relationship<*, *>) =
                when (relationship.direction) {
                    Relationship.Direction.FORWARD -> out(relationship.name)
                    Relationship.Direction.BACKWARD -> `in`(relationship.name)
                    null -> both(relationship.name)
                }
    }
}
