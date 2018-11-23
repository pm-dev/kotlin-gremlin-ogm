package org.apache.tinkerpop.gremlin.ogm.mappers

import org.apache.tinkerpop.gremlin.ogm.elements.BasicEdge
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.ConflictingEdge
import org.apache.tinkerpop.gremlin.ogm.exceptions.ObjectNotSaved
import org.apache.tinkerpop.gremlin.ogm.exceptions.UnregisteredClass
import org.apache.tinkerpop.gremlin.ogm.extensions.setProperties
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import org.apache.tinkerpop.gremlin.ogm.reflection.EdgeDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.VertexDescription
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.slf4j.LoggerFactory

internal class EdgeSerializer(
        private val graphDescription: GraphDescription,
        private val g: GraphTraversalSource
) {

    operator fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> invoke(from: E): org.apache.tinkerpop.gremlin.structure.Edge {
        val edgeClass = from::class
        val edgeDescription: EdgeDescription<FROM, TO, E>? = if (graphDescription.edgeClasses.contains(edgeClass)) graphDescription.getEdgeDescription(edgeClass) else null
        val fromVertexDescription: VertexDescription<FROM> = graphDescription.getVertexDescription(from.from::class)
        val toVertexDescription: VertexDescription<TO> = graphDescription.getVertexDescription(from.to::class)
        val objectSerializer = edgeDescription?.let { ObjectSerializer(graphDescription, it) }
        val fromVertex = from.from
        val toVertex = from.to
        val relationship = when {
            graphDescription.edgeClasses.contains(from::class) -> graphDescription.getEdgeDescription(from::class).relationship
            from is BasicEdge<*, *> -> from.relationship
            else -> throw UnregisteredClass(from::class)
        }
        val fromID = fromVertexDescription.id.property.get(fromVertex) ?: throw ObjectNotSaved(fromVertex)
        val toID = toVertexDescription.id.property.get(toVertex) ?: throw ObjectNotSaved(toVertex)
        val existingEdge = ((g.V(fromID) out relationship).hasId(toID) `in` relationship).hasId(fromID) outE relationship
        val conflictingFrom = if (relationship is Relationship.ToOne) g.V(fromID) outE relationship else null
        val conflictingTo = if (relationship is Relationship.FromOne) g.V(toID) inE relationship else null
        val createEdge = when (relationship.direction) {
            Relationship.Direction.BACKWARD -> g.V(toID)
                    .let { if (conflictingFrom == null) it else it.not(conflictingFrom) }
                    .let { if (conflictingTo == null) it else it.not(conflictingTo) }
                    .addE(relationship.name).to(g.V(fromID))
            else -> g.V(fromID)
                    .let { if (conflictingFrom == null) it else it.not(conflictingFrom) }
                    .let { if (conflictingTo == null) it else it.not(conflictingTo) }
                    .addE(relationship.name).to(g.V(toID))
        }
        val createOrGetEdge = g.inject<Any>(0).coalesce(existingEdge.sideEffect {
            logger.debug("Updating edge ${relationship.name} from $fromVertex to $toVertex.")
        }, createEdge.sideEffect {
            logger.debug("Creating edge ${relationship.name} from $fromVertex to $toVertex.")
        }).map { edge ->
            objectSerializer?.let {
                val serializedProperties = it(from)
                edge.get().setProperties(serializedProperties)
            } ?: edge.get()
        }
        if (!createOrGetEdge.hasNext()) throw ConflictingEdge(fromVertex, toVertex, relationship.name)
        return createOrGetEdge.toList().single()
    }

    private infix fun GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Vertex>.out(relationship: Relationship<*, *>): GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Vertex> =
            when (relationship.direction) {
                Relationship.Direction.FORWARD -> out(relationship.name)
                Relationship.Direction.BACKWARD -> `in`(relationship.name)
                null -> both(relationship.name)
            }

    private infix fun GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Vertex>.`in`(relationship: Relationship<*, *>): GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Vertex> =
            when (relationship.direction) {
                Relationship.Direction.FORWARD -> `in`(relationship.name)
                Relationship.Direction.BACKWARD -> out(relationship.name)
                null -> both(relationship.name)
            }

    private infix fun GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Vertex>.outE(relationship: Relationship<*, *>): GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Edge> =
            when (relationship.direction) {
                Relationship.Direction.FORWARD -> outE(relationship.name)
                Relationship.Direction.BACKWARD -> inE(relationship.name)
                null -> bothE(relationship.name)
            }

    private infix fun GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Vertex>.inE(relationship: Relationship<*, *>): GraphTraversal<*, org.apache.tinkerpop.gremlin.structure.Edge> =
            when (relationship.direction) {
                Relationship.Direction.FORWARD -> inE(relationship.name)
                Relationship.Direction.BACKWARD -> outE(relationship.name)
                null -> bothE(relationship.name)
            }

    companion object {

        private val logger = LoggerFactory.getLogger(EdgeSerializer::class.java)
    }
}
