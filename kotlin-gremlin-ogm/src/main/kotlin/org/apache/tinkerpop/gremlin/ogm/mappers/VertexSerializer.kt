package org.apache.tinkerpop.gremlin.ogm.mappers

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.IDNotFound
import org.apache.tinkerpop.gremlin.ogm.extensions.setProperties
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

internal class VertexSerializer(
        private val graphDescription: GraphDescription,
        private val g: GraphTraversalSource
) {

    operator fun <T : Vertex> invoke(from: T): org.apache.tinkerpop.gremlin.structure.Vertex {
        val vertexDescription = graphDescription.getVertexDescription(from::class)
        val id = vertexDescription.id.property.get(from)
        val traversal = when (id) {
            null -> g.addV(vertexDescription.label)
            else -> g.V(id)
        }
        val objectSerializer = ObjectSerializer(graphDescription, vertexDescription)
        return traversal.map { vertex ->
            val serializedProperties = objectSerializer(from)
            vertex.get().setProperties(serializedProperties)
        }.toList().singleOrNull() ?: throw IDNotFound(from, id)
    }
}
