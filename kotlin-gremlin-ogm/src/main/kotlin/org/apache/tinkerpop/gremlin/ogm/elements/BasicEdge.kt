package org.apache.tinkerpop.gremlin.ogm.elements

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship


internal class BasicEdge<FROM : Vertex, TO : Vertex>(
        override val from: FROM,
        override val to: TO,
        val relationship: Relationship<FROM, TO>
) : Edge<FROM, TO>
