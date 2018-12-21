package org.apache.tinkerpop.gremlin.ogm.mappers

import org.apache.tinkerpop.gremlin.ogm.GraphVertex
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.extensions.getProperties
import org.apache.tinkerpop.gremlin.ogm.mappers.EdgeDeserializer.Companion.idTag
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription

internal class VertexDeserializer(
        private val graphDescription: GraphDescription
) {

    operator fun <T : Vertex> invoke(from: GraphVertex): T {
        val vertexDescription = graphDescription.getVertexDescription<T>(from.label())
        val objectDeserializer = ObjectDeserializer(graphDescription, vertexDescription, Pair(idTag, vertexDescription.id))
        return objectDeserializer(from.getProperties() + Pair(idTag, from.id()))
    }
}
