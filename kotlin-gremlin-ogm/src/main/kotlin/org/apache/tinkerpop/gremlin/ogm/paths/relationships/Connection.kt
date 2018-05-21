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
interface Connection<OUT : Any, IN : Any> : Path<OUT, IN> {

    /**
     * This Connection in reverse.
     */
    val inverse: Connection<IN, OUT>

    /**
     * The individual [Relationship]s that create this connection
     */
    fun relationships(): List<Relationship<*, *>>

    override fun path() = listOf(this)

    override fun invoke(from: StepTraverser<OUT>): GraphTraversal<*, IN> {
        val serialized = from.traversal.map { fromObject ->
            from.vertexMapper.forwardMap(fromObject.get())
        }
        val traversed = relationships().fold(initial = serialized) { traversal, relationship ->
            if (relationship is Relationship.ToSingle) {
                traversal.coalesce(Companion.to(relationship), throwMissingEdge(relationship))
            } else {
                traversal.to(relationship)
            }
        }
        return traversed.map { toVertex -> from.vertexMapper.inverseMap(toVertex.get()) as IN }
    }

    interface FromOne<OUT : Any, IN : Any> : Connection<OUT, IN> {

        override val inverse: ToOne<IN, OUT>
    }

    interface FromOptional<OUT : Any, IN : Any> : FromOne<OUT, IN> {

        override val inverse: ToOptional<IN, OUT>
    }

    interface FromSingle<OUT : Any, IN : Any> : FromOne<OUT, IN> {

        override val inverse: ToSingle<IN, OUT>
    }

    interface FromMany<OUT : Any, IN : Any> : Connection<OUT, IN> {

        override val inverse: ToMany<IN, OUT>
    }

    interface ToOne<OUT : Any, IN : Any> : Connection<OUT, IN>, Path.ToOne<OUT, IN> {

        override val inverse: FromOne<IN, OUT>
    }

    interface ToOptional<OUT : Any, IN : Any> : ToOne<OUT, IN>, Path.ToOptional<OUT, IN> {

        override val inverse: FromOptional<IN, OUT>
    }

    interface ToSingle<OUT : Any, IN : Any> : ToOne<OUT, IN>, Path.ToSingle<OUT, IN> {

        override val inverse: FromSingle<IN, OUT>
    }

    interface ToMany<OUT : Any, IN : Any> : Connection<OUT, IN>, Path.ToMany<OUT, IN> {

        override val inverse: FromMany<IN, OUT>
    }

    interface OneToOne<OUT : Any, IN : Any> : FromOne<OUT, IN>, ToOne<OUT, IN> {

        override val inverse: OneToOne<IN, OUT>
    }

    interface OneToOptional<OUT : Any, IN : Any> : OneToOne<OUT, IN>, ToOptional<OUT, IN> {

        override val inverse: OptionalToOne<IN, OUT>
    }

    interface OneToSingle<OUT : Any, IN : Any> : OneToOne<OUT, IN>, ToSingle<OUT, IN> {

        override val inverse: SingleToOne<IN, OUT>
    }

    interface OptionalToOne<OUT : Any, IN : Any> : FromOptional<OUT, IN>, OneToOne<OUT, IN> {

        override val inverse: OneToOptional<IN, OUT>
    }

    interface SingleToOne<OUT : Any, IN : Any> : FromSingle<OUT, IN>, OneToOne<OUT, IN> {

        override val inverse: OneToSingle<IN, OUT>
    }

    interface OneToMany<OUT : Any, IN : Any> : FromOne<OUT, IN>, ToMany<OUT, IN> {

        override val inverse: ManyToOne<IN, OUT>
    }

    interface ManyToOne<OUT : Any, IN : Any> : FromMany<OUT, IN>, ToOne<OUT, IN> {

        override val inverse: OneToMany<IN, OUT>
    }

    interface OptionalToOptional<OUT : Any, IN : Any> : OptionalToOne<OUT, IN>, OneToOptional<OUT, IN> {

        override val inverse: OptionalToOptional<IN, OUT>
    }

    interface OptionalToSingle<OUT : Any, IN : Any> : OptionalToOne<OUT, IN>, OneToSingle<OUT, IN> {

        override val inverse: SingleToOptional<IN, OUT>
    }

    interface SingleToOptional<OUT : Any, IN : Any> : SingleToOne<OUT, IN>, OneToOptional<OUT, IN> {

        override val inverse: OptionalToSingle<IN, OUT>
    }

    interface SingleToSingle<OUT : Any, IN : Any> : SingleToOne<OUT, IN>, OneToSingle<OUT, IN> {

        override val inverse: SingleToSingle<IN, OUT>
    }

    interface OptionalToMany<OUT : Any, IN : Any> : FromOptional<OUT, IN>, OneToMany<OUT, IN> {

        override val inverse: ManyToOptional<IN, OUT>
    }

    interface SingleToMany<OUT : Any, IN : Any> : FromSingle<OUT, IN>, OneToMany<OUT, IN> {

        override val inverse: ManyToSingle<IN, OUT>
    }

    interface ManyToOptional<OUT : Any, IN : Any> : ManyToOne<OUT, IN>, ToOptional<OUT, IN> {

        override val inverse: OptionalToMany<IN, OUT>
    }

    interface ManyToSingle<OUT : Any, IN : Any> : ManyToOne<OUT, IN>, ToSingle<OUT, IN> {

        override val inverse: SingleToMany<IN, OUT>
    }

    interface ManyToMany<OUT : Any, IN : Any> : FromMany<OUT, IN>, ToMany<OUT, IN> {

        override val inverse: ManyToMany<IN, OUT>
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
