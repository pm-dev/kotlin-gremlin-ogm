package org.apache.tinkerpop.gremlin.ogm.steps.edgestep

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec.EdgeSpec

data class EdgeStepToOptional<FROM : Vertex, out TO : Vertex, E : Edge<FROM, TO>>(
        override val edgeSpec: EdgeSpec.ToOptional<FROM, out TO>
) : EdgeStep.ToOptional<FROM, TO, E>
