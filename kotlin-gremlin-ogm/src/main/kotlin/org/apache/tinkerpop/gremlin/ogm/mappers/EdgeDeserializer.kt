package org.apache.tinkerpop.gremlin.ogm.mappers

import org.apache.tinkerpop.gremlin.ogm.elements.BasicEdge
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.extensions.getProperties
import org.apache.tinkerpop.gremlin.ogm.reflection.EdgeDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription

internal class EdgeDeserializer(private val graphDescription: GraphDescription) {

    operator fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> invoke(from: org.apache.tinkerpop.gremlin.structure.Edge): E {
        val edgeDescription: EdgeDescription<FROM, TO, E>? = graphDescription.getEdgeDescription(from.label())
        val objectDeserializer = edgeDescription?.let {
            ObjectDeserializer(
                    graphDescription,
                    edgeDescription,
                    idTag to edgeDescription.id,
                    toVertexTag to edgeDescription.toVertex,
                    fromVertexTag to edgeDescription.fromVertex)
        }
        val toVertex = VertexDeserializer(graphDescription)<TO>(from.inVertex())
        val fromVertex = VertexDeserializer(graphDescription)<FROM>(from.outVertex())
        return objectDeserializer?.let {
            val serializedProperties = from.getProperties() +
                    (idTag to from.id()) +
                    (toVertexTag to toVertex) +
                    (fromVertexTag to fromVertex)
            return it(serializedProperties)
        } ?: kotlin.run {
            val relationship = graphDescription.getEdgeRelationship<FROM, TO>(from.label())
            @Suppress("UNCHECKED_CAST")
            BasicEdge(fromVertex, toVertex, relationship) as E
        }
    }
    
    companion object {
        /**
         * This is a reserved property key used to mark the property annotated with @ID.
         * This means Clients may not use @Property(name = "id")
         */
        internal const val idTag = "id"
        internal const val toVertexTag = "CE1C7396-A7D6-4584-98DA-B0E965A35034"
        internal const val fromVertexTag = "4A5F116C-B3BB-47AB-B1E3-7DBC24148BED"
    }
}
