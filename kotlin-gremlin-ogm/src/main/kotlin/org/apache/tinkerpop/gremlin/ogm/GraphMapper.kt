@file:Suppress("unused")

package org.apache.tinkerpop.gremlin.ogm

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.UnregisteredClass
import org.apache.tinkerpop.gremlin.ogm.extensions.toMultiMap
import org.apache.tinkerpop.gremlin.ogm.mappers.EdgeDeserializer
import org.apache.tinkerpop.gremlin.ogm.mappers.EdgeSerializer
import org.apache.tinkerpop.gremlin.ogm.mappers.VertexDeserializer
import org.apache.tinkerpop.gremlin.ogm.mappers.VertexSerializer
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.steps.StepTraverser
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * The main object a client's application will interact with.
 * This interface provides the ability to map objects from a clients domain to the graph and back.
 */
interface GraphMapper {

    val graphDescription: GraphDescription

    val g: GraphTraversalSource

    /**
     * Gets a graph traversal that emits vertices for given ids.
     * No exception is thrown for ids that don't correspond to a vertex, thus the number of vertices the traversal emits
     * may be less than the number of ids.
     */
    fun <V : Vertex> V(ids: Collection<Any>): GraphTraversal<*, V> {
        if (ids.isEmpty()) {
            return g.inject<V>()
        }
        return ids
                .map { id ->
                    g.V(id)
                }
                .reduce { traversal1, traversal2 ->
                    traversal1.union(traversal2)
                }
                .map { vertex ->
                    deserializeV<V>(vertex.get())
                }
    }

    /**
     * Get a traversal that emits all vertices for class V (which has been registered as a vertex with this GraphMapper).
     * V may be a superclass of classes registered as a vertex.
     */
    fun <V : Vertex> V(kClass: KClass<V>): GraphTraversal<*, V> {
        val labels = graphDescription.vertexClasses.filter { vertexKClass ->
            vertexKClass.isSubclassOf(kClass)
        }.map { vertexClass ->
            graphDescription.getVertexDescription(vertexClass).label
        }
        if (labels.isEmpty()) throw UnregisteredClass(kClass)
        logger.debug("Will get all vertices with labels $labels")
        return labels.map { label ->
            g.V().hasLabel(label)
        }.reduce { traversal1, traversal2 ->
            g.V().union(traversal1, traversal2)
        }.map { vertex ->
            deserializeV<V>(vertex.get())
        }
    }

    /**
     * Gets a graph traversal that emits edges for given ids.
     * No exception is thrown for ids that don't correspond to an edge, thus the number of edges the traversal emits
     * may be less than the number of ids.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(ids: Collection<Any>): GraphTraversal<*, E> {
        if (ids.isEmpty()) {
            return g.inject<E>()
        }
        return ids
                .map { id ->
                    g.E(id)
                }
                .reduce { traversal1, traversal2 ->
                    traversal1.union(traversal2)
                }
                .map { edge ->
                    deserializeE<FROM, TO, E>(edge.get())
                }
    }

    /**
     * Get a traversal that emits all edges for class E (which has been registered with a relationship as
     * an edge with this GraphMapper).
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(kClass: KClass<E>): GraphTraversal<*, E> {
        val edgeDescription = graphDescription.getEdgeDescription(kClass)
        logger.debug("Will get all edges with label ${edgeDescription.label}")
        return g.E()
                .hasLabel(edgeDescription.label)
                .map { vertex ->
                    deserializeE<FROM, TO, E>(vertex.get())
                }
    }

    /**
     * Saves an object annotated with @Element to the graph. If property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID. If the property annotated with @ID is non-null,
     * but the vertex cannot be found, an exception is thrown.
     */
    fun <V : Vertex> saveV(deserialized: V): V {
        val serialized = serializeV(deserialized)
        logger.debug("Saved vertex with id ${serialized.id()}")
        return deserializeV(serialized)
    }

    /**
     * Saves vertices to the graph. If the property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID. If the property annotated with @ID is non-null,
     * but the vertex cannot be found, an exception is thrown.
     */
    fun <V : Vertex> saveV(objs: Iterable<V>): List<V> = objs.map { saveV(it) }

    /**
     * Saves edges to the graph. If the property annotated with @ID is null,
     * a new edge will be created, otherwise this object will overwrite the current edge with that id.
     * The returned object will always have a non-null @ID.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> saveE(edge: E): E {
        val serialized = serializeE(edge)
        logger.debug("Saved edge with id ${serialized.id()}")
        return deserializeE(serialized)
    }

    fun <FROM : Vertex, TO> traverse(froms: Iterable<FROM>, path: Path<FROM, TO>): Map<FROM, List<TO>> {
        if (froms.none()) {
            return emptyMap()
        }
        val traversalStart = froms.fold(initial = g.inject<FROM>()) { traversal, from ->
            traversal.inject(from).`as`(fromKey)
        }
        @Suppress("UNCHECKED_CAST")
        val traversed = path.path().fold(initial = traversalStart as GraphTraversal<Any, Any>) { traversal, step ->
            step as Path<Any, Any>
            step(StepTraverser(traversal, this)) as GraphTraversal<Any, Any>
        }
        @Suppress("UNCHECKED_CAST")
        return traversed.`as`(toKey).select<Any>(fromKey, toKey).toMultiMap(froms) {
            val from = it[fromKey] as FROM
            val to = it[toKey] as TO
            logger.debug("Traversed from $from to $to")
            from to to
        }
    }

    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> deserializeE(edge: org.apache.tinkerpop.gremlin.structure.Edge): E =
            EdgeDeserializer(graphDescription)(edge)

    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> serializeE(edge: E): org.apache.tinkerpop.gremlin.structure.Edge =
            EdgeSerializer(graphDescription, g)(edge)

    fun <V : Vertex> serializeV(deserialized: V): org.apache.tinkerpop.gremlin.structure.Vertex =
            VertexSerializer(graphDescription, g)(deserialized)

    fun <V : Vertex> deserializeV(serialized: org.apache.tinkerpop.gremlin.structure.Vertex): V =
            VertexDeserializer(graphDescription)(serialized)

    companion object {

        private const val fromKey = "from"

        private const val toKey = "to"

        private val logger = LoggerFactory.getLogger(GraphMapper::class.java)
    }
}
