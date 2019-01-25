package org.apache.tinkerpop.gremlin.ogm.steps.bound.single.edgespec

import org.apache.tinkerpop.gremlin.ogm.elements.BasicEdge
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.SingleBoundStep
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec.EdgeSpec

/**
 * A [SingleBoundStep] whose step is a [EdgeSpec]
 */
interface SingleBoundEdgeSpec<FROM : Vertex, TO : Vertex> : SingleBoundStep<FROM, TO> {

    override val step: EdgeSpec<FROM, TO>

    infix fun to(to: TO): Edge<FROM, TO> = BasicEdge(from = from, to = to, spec = step)

    interface ToOne<FROM : Vertex, TO : Vertex> : SingleBoundEdgeSpec<FROM, TO>, SingleBoundStep.ToOne<FROM, TO> {

        override val step: EdgeSpec.ToOne<FROM, TO>
    }

    /**
     * A [SingleBoundEdgeSpec] whose spec is a [Relationship.ToSingle]
     */
    interface ToSingle<FROM : Vertex, TO : Vertex> : ToOne<FROM, TO>, SingleBoundStep.ToSingle<FROM, TO> {

        override val step: EdgeSpec.ToSingle<FROM, TO>
    }

    /**
     * A [SingleBoundEdgeSpec] whose spec is a [Relationship.ToOptional]
     */
    interface ToOptional<FROM : Vertex, TO : Vertex> : ToOne<FROM, TO>, SingleBoundStep.ToOptional<FROM, TO> {

        override val step: EdgeSpec.ToOptional<FROM, TO>
    }

    /**
     * A [SingleBoundEdgeSpec] whose spec is a [Relationship.ToMany]
     */
    interface ToMany<FROM : Vertex, TO : Vertex> : SingleBoundEdgeSpec<FROM, TO>, SingleBoundStep.ToMany<FROM, TO> {

        override val step: EdgeSpec.ToMany<FROM, TO>

        infix fun to(tos: Collection<TO>): List<Edge<FROM, TO>> = tos.map { BasicEdge(from = from, to = it, spec = step) }

        fun to(vararg tos: TO): List<Edge<FROM, TO>> = tos.map { BasicEdge(from = from, to = it, spec = step) }
    }
}
