package org.apache.tinkerpop.gremlin.ogm.steps.edgestep

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec.EdgeSpec

data class EdgeStepToMany<FROM : Vertex, out TO : Vertex, E : Edge<FROM, TO>>(
        override val edgeSpec: EdgeSpec.ToMany<FROM, out TO>
) : EdgeStep.ToMany<FROM, TO, E>
