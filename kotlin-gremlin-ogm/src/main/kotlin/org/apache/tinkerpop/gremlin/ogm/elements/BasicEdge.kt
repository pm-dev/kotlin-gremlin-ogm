package org.apache.tinkerpop.gremlin.ogm.elements

import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.edgespec.EdgeSpec


internal class BasicEdge<FROM : Vertex, TO : Vertex>(
        override val from: FROM,
        override val to: TO,
        val spec: EdgeSpec<FROM, TO>
) : Edge<FROM, TO>
