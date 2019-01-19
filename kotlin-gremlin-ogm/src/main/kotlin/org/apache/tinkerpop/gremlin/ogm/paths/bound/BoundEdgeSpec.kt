package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.BasicEdge
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.Relationship
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.edgespec.EdgeSpec

interface BoundEdgeSpec<FROM : Vertex, TO : Vertex> : BoundStep<FROM, TO> {

    override val step: EdgeSpec<FROM, TO>

    infix fun to(to: TO): List<Edge<FROM, TO>> = froms.map { BasicEdge(from = it, to = to, spec = step) }

    /**
     * A [BoundEdgeSpec] where the spec is a [Relationship.ToOne]
     */
    interface ToOne<FROM : Vertex, TO : Vertex> : BoundEdgeSpec<FROM, TO>, BoundStep.ToOne<FROM, TO> {

        override val step: EdgeSpec.ToOne<FROM, TO>
    }

    /**
     * A [BoundEdgeSpec] where the spec is a [Relationship.ToMany]
     */
    interface ToMany<FROM : Vertex, TO : Vertex> : BoundEdgeSpec<FROM, TO>, BoundStep.ToMany<FROM, TO> {

        override val step: EdgeSpec.ToMany<FROM, TO>
    }

    /**
     * A [BoundEdgeSpec] where the spec is a [Relationship.ToSingle]
     */
    interface ToSingle<FROM : Vertex, TO : Vertex> : ToOne<FROM, TO>, BoundStep.ToSingle<FROM, TO> {

        override val step: EdgeSpec.ToSingle<FROM, TO>
    }

    /**
     * A [BoundEdgeSpec] where the spec is a [Relationship.ToOptional]
     */
    interface ToOptional<FROM : Vertex, TO : Vertex> : ToOne<FROM, TO>, BoundStep.ToOne<FROM, TO> {

        override val step: EdgeSpec.ToOptional<FROM, TO>
    }
}
