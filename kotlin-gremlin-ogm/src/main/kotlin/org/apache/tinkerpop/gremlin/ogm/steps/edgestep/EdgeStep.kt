package org.apache.tinkerpop.gremlin.ogm.steps.edgestep

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.Step
import org.apache.tinkerpop.gremlin.ogm.steps.StepTraverser
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec.EdgeSpec
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

interface EdgeStep<FROM : Vertex, out TO : Vertex, E : Edge<FROM, TO>> : Step<FROM, E> {

    val edgeSpec: EdgeSpec<FROM, out TO>

    interface ToSingle<FROM : Vertex, out TO : Vertex, E : Edge<FROM, TO>> : EdgeStep<FROM, TO, E>, Step.ToSingle<FROM, E> {

        override val edgeSpec: EdgeSpec.ToSingle<FROM, out TO>

        override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, E> =
                from.traversal
                        .map { from.graphMapper.serialize(it.get()) }
                        .outE(edgeSpec.name)
                        .map { from.graphMapper.deserialize<FROM, TO, E>(it.get()) }
    }

    interface ToOptional<FROM : Vertex, out TO : Vertex, E : Edge<FROM, TO>> : EdgeStep<FROM, TO, E>, Step.ToOptional<FROM, E> {

        override val edgeSpec: EdgeSpec.ToOptional<FROM, out TO>

        override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, E> =
                from.traversal
                        .map { from.graphMapper.serialize(it.get()) }
                        .outE(edgeSpec.name)
                        .map { from.graphMapper.deserialize<FROM, TO, E>(it.get()) }
    }

    interface ToMany<FROM : Vertex, out TO : Vertex, E : Edge<FROM, TO>> : EdgeStep<FROM, TO, E>, Step.ToMany<FROM, E> {

        override val edgeSpec: EdgeSpec.ToMany<FROM, out TO>

        override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, E> =
                from.traversal
                        .map { from.graphMapper.serialize(it.get()) }
                        .outE(edgeSpec.name)
                        .map { from.graphMapper.deserialize<FROM, TO, E>(it.get()) }
    }
}
