package org.apache.tinkerpop.gremlin.ogm.mappers

import org.apache.tinkerpop.gremlin.ogm.GraphEdge
import org.apache.tinkerpop.gremlin.ogm.GraphVertex
import org.apache.tinkerpop.gremlin.ogm.elements.BasicEdge
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.ConflictingEdge
import org.apache.tinkerpop.gremlin.ogm.exceptions.ObjectNotSaved
import org.apache.tinkerpop.gremlin.ogm.exceptions.UnregisteredClass
import org.apache.tinkerpop.gremlin.ogm.extensions.setProperties
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec.EdgeSpec
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
        val edgeSpec = when {
            graphDescription.edgeClasses.contains(from::class) -> graphDescription.getEdgeDescription(from::class).edgeSpec
            from is BasicEdge<*, *> -> from.spec
            else -> throw UnregisteredClass(from::class)
        }
        val fromID = fromVertexDescription.id.property.get(fromVertex) ?: throw ObjectNotSaved(fromVertex)
        val toID = toVertexDescription.id.property.get(toVertex) ?: throw ObjectNotSaved(toVertex)
        val existingEdge = ((g.V(fromID) out edgeSpec).hasId(toID) `in` edgeSpec).hasId(fromID) outE edgeSpec
        val conflictingFrom = if (edgeSpec is EdgeSpec.ToOne) g.V(fromID) outE edgeSpec else null
        val conflictingTo = if (edgeSpec is EdgeSpec.FromOne) g.V(toID) inE edgeSpec else null
        val createEdge = when (edgeSpec.direction) {
            EdgeSpec.Direction.BACKWARD -> g.V(toID)
                    .let { if (conflictingFrom == null) it else it.not(conflictingFrom) }
                    .let { if (conflictingTo == null) it else it.not(conflictingTo) }
                    .addE(edgeSpec.name).to(g.V(fromID))
            else -> g.V(fromID)
                    .let { if (conflictingFrom == null) it else it.not(conflictingFrom) }
                    .let { if (conflictingTo == null) it else it.not(conflictingTo) }
                    .addE(edgeSpec.name).to(g.V(toID))
        }
        val createOrGetEdge = g.inject<Any>(0).coalesce(existingEdge.sideEffect {
            logger.debug("Updating edge ${edgeSpec.name} from $fromVertex to $toVertex.")
        }, createEdge.sideEffect {
            logger.debug("Creating edge ${edgeSpec.name} from $fromVertex to $toVertex.")
        }).map { edge ->
            objectSerializer?.let {
                val serializedProperties = it(from)
                edge.get().setProperties(serializedProperties)
            } ?: edge.get()
        }
        if (!createOrGetEdge.hasNext()) throw ConflictingEdge(fromVertex, toVertex, edgeSpec.name)
        return createOrGetEdge.toList().single()
    }

    private infix fun GraphTraversal<*, GraphVertex>.out(edgeSpec: EdgeSpec<*, *>): GraphTraversal<*, GraphVertex> =
            when (edgeSpec.direction) {
                EdgeSpec.Direction.FORWARD -> out(edgeSpec.name)
                EdgeSpec.Direction.BACKWARD -> `in`(edgeSpec.name)
                null -> both(edgeSpec.name)
            }

    private infix fun GraphTraversal<*, GraphVertex>.`in`(edgeSpec: EdgeSpec<*, *>): GraphTraversal<*, GraphVertex> =
            when (edgeSpec.direction) {
                EdgeSpec.Direction.FORWARD -> `in`(edgeSpec.name)
                EdgeSpec.Direction.BACKWARD -> out(edgeSpec.name)
                null -> both(edgeSpec.name)
            }

    private infix fun GraphTraversal<*, GraphVertex>.outE(edgeSpec: EdgeSpec<*, *>): GraphTraversal<*, GraphEdge> =
            when (edgeSpec.direction) {
                EdgeSpec.Direction.FORWARD -> outE(edgeSpec.name)
                EdgeSpec.Direction.BACKWARD -> inE(edgeSpec.name)
                null -> bothE(edgeSpec.name)
            }

    private infix fun GraphTraversal<*, GraphVertex>.inE(edgeSpec: EdgeSpec<*, *>): GraphTraversal<*, GraphEdge> =
            when (edgeSpec.direction) {
                EdgeSpec.Direction.FORWARD -> inE(edgeSpec.name)
                EdgeSpec.Direction.BACKWARD -> outE(edgeSpec.name)
                null -> bothE(edgeSpec.name)
            }

    companion object {

        private val logger = LoggerFactory.getLogger(EdgeSerializer::class.java)
    }
}
