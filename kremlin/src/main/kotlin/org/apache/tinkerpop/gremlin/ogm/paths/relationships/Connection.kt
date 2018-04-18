@file:Suppress("UNCHECKED_CAST")

package org.apache.tinkerpop.gremlin.ogm.paths.relationships

import org.apache.tinkerpop.gremlin.ogm.exceptions.MissingEdge
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.steps.StepTraverser
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex

/**
 * A path through the graph starting and ending at a vertex, which spans one or more relationships.
 * A special property of Connections is that they can be reversed.
 */
interface Connection<FROM : Any, TO : Any> : Path<FROM, TO> {

    /**
     * This Connection in reverse.
     */
    val inverse: Connection<TO, FROM>

    /**
     * The individual [Relationship]s that create this connection
     */
    fun relationships(): List<Relationship<*, *>>

    override fun path() = listOf(this)

    override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, TO> {
        val serialized = from.traversal.map { fromObject ->
            from.mapper.forwardMap(fromObject.get())
        }
        val traversed = relationships().fold(initial = serialized) { traversal, relationship ->
            if (relationship is Relationship.ToSingle) {
                traversal.coalesce(Companion.to(relationship), throwMissingEdge(relationship))
            } else {
                traversal.to(relationship)
            }
        }
        return traversed.map { toVertex -> from.mapper.inverseMap(toVertex.get()) as TO }
    }

    interface FromOne<FROM : Any, TO : Any> : Connection<FROM, TO> {

        override val inverse: ToOne<TO, FROM>
    }

    interface FromOptional<FROM : Any, TO : Any> : FromOne<FROM, TO> {

        override val inverse: ToOptional<TO, FROM>
    }

    interface FromSingle<FROM : Any, TO : Any> : FromOne<FROM, TO> {

        override val inverse: ToSingle<TO, FROM>
    }

    interface FromMany<FROM : Any, TO : Any> : Connection<FROM, TO> {

        override val inverse: ToMany<TO, FROM>
    }

    interface ToOne<FROM : Any, TO : Any> : Connection<FROM, TO>, Path.ToOne<FROM, TO> {

        override val inverse: FromOne<TO, FROM>
    }

    interface ToOptional<FROM : Any, TO : Any> : ToOne<FROM, TO>, Path.ToOptional<FROM, TO> {

        override val inverse: FromOptional<TO, FROM>
    }

    interface ToSingle<FROM : Any, TO : Any> : ToOne<FROM, TO>, Path.ToSingle<FROM, TO> {

        override val inverse: FromSingle<TO, FROM>
    }

    interface ToMany<FROM : Any, TO : Any> : Connection<FROM, TO>, Path.ToMany<FROM, TO> {

        override val inverse: FromMany<TO, FROM>
    }

    interface OneToOne<FROM : Any, TO : Any> : FromOne<FROM, TO>, ToOne<FROM, TO> {

        override val inverse: OneToOne<TO, FROM>
    }

    interface OneToOptional<FROM : Any, TO : Any> : OneToOne<FROM, TO>, ToOptional<FROM, TO> {

        override val inverse: OptionalToOne<TO, FROM>
    }

    interface OneToSingle<FROM : Any, TO : Any> : OneToOne<FROM, TO>, ToSingle<FROM, TO> {

        override val inverse: SingleToOne<TO, FROM>
    }

    interface OptionalToOne<FROM : Any, TO : Any> : FromOptional<FROM, TO>, OneToOne<FROM, TO> {

        override val inverse: OneToOptional<TO, FROM>
    }

    interface SingleToOne<FROM : Any, TO : Any> : FromSingle<FROM, TO>, OneToOne<FROM, TO> {

        override val inverse: OneToSingle<TO, FROM>
    }

    interface OneToMany<FROM : Any, TO : Any> : FromOne<FROM, TO>, ToMany<FROM, TO> {

        override val inverse: ManyToOne<TO, FROM>
    }

    interface ManyToOne<FROM : Any, TO : Any> : FromMany<FROM, TO>, ToOne<FROM, TO> {

        override val inverse: OneToMany<TO, FROM>
    }

    interface OptionalToOptional<FROM : Any, TO : Any> : OptionalToOne<FROM, TO>, OneToOptional<FROM, TO> {

        override val inverse: OptionalToOptional<TO, FROM>
    }

    interface OptionalToSingle<FROM : Any, TO : Any> : OptionalToOne<FROM, TO>, OneToSingle<FROM, TO> {

        override val inverse: SingleToOptional<TO, FROM>
    }

    interface SingleToOptional<FROM : Any, TO : Any> : SingleToOne<FROM, TO>, OneToOptional<FROM, TO> {

        override val inverse: OptionalToSingle<TO, FROM>
    }

    interface SingleToSingle<FROM : Any, TO : Any> : SingleToOne<FROM, TO>, OneToSingle<FROM, TO> {

        override val inverse: SingleToSingle<TO, FROM>
    }

    interface OptionalToMany<FROM : Any, TO : Any> : FromOptional<FROM, TO>, OneToMany<FROM, TO> {

        override val inverse: ManyToOptional<TO, FROM>
    }

    interface SingleToMany<FROM : Any, TO : Any> : FromSingle<FROM, TO>, OneToMany<FROM, TO> {

        override val inverse: ManyToSingle<TO, FROM>
    }

    interface ManyToOptional<FROM : Any, TO : Any> : ManyToOne<FROM, TO>, ToOptional<FROM, TO> {

        override val inverse: OptionalToMany<TO, FROM>
    }

    interface ManyToSingle<FROM : Any, TO : Any> : ManyToOne<FROM, TO>, ToSingle<FROM, TO> {

        override val inverse: SingleToMany<TO, FROM>
    }

    interface ManyToMany<FROM : Any, TO : Any> : FromMany<FROM, TO>, ToMany<FROM, TO> {

        override val inverse: ManyToMany<TO, FROM>
    }

    companion object {

        private fun throwMissingEdge(relationship: Relationship<*, *>): GraphTraversal<*, Vertex> =
            DefaultGraphTraversal<Any, Vertex>().map {
                throw MissingEdge(it.get(), relationship.name)
            } as GraphTraversal<Any, Vertex>

        private fun to(relationship: Relationship<*, *>): GraphTraversal<*, Vertex> =
                DefaultGraphTraversal<Any, Vertex>().to(relationship)

        private fun GraphTraversal<*, Vertex>.to(relationship: Relationship<*, *>) =
                when (relationship.direction) {
                    Relationship.Direction.FORWARD -> out(relationship.name)
                    Relationship.Direction.BACKWARD -> `in`(relationship.name)
                    null -> both(relationship.name)
                }
    }
}
