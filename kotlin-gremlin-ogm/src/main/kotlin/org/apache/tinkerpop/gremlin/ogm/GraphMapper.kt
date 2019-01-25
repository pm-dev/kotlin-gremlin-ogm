@file:Suppress("unused")

package org.apache.tinkerpop.gremlin.ogm

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.UnregisteredClass
import org.apache.tinkerpop.gremlin.ogm.extensions.toMultiMap
import org.apache.tinkerpop.gremlin.ogm.extensions.toOptionalMap
import org.apache.tinkerpop.gremlin.ogm.extensions.toSingleMap
import org.apache.tinkerpop.gremlin.ogm.mappers.EdgeDeserializer
import org.apache.tinkerpop.gremlin.ogm.mappers.EdgeSerializer
import org.apache.tinkerpop.gremlin.ogm.mappers.VertexDeserializer
import org.apache.tinkerpop.gremlin.ogm.mappers.VertexSerializer
import org.apache.tinkerpop.gremlin.ogm.steps.bound.BoundStep
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.SingleBoundStep
import org.apache.tinkerpop.gremlin.ogm.steps.StepTraverser
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * The main object a client's application will interact with.
 * This interface provides the ability to map objects from a clients domain to the graph and back.
 * The default implementation of all methods are thread-safe.
 */
interface GraphMapper {

    val graphDescription: GraphDescription

    val g: GraphTraversalSource

    /**
     *
     * Vertices
     *
     */

    /**
     * Gets a graph g that emits a vertex with a given id.
     */
    fun <V : Vertex> V(id: Any): V? = V<V>(listOf(id)).singleOrNull()

    /**
     * Gets a graph g that emits vertices for given ids.
     * No exception is thrown for ids that don't correspond to a vertex, thus the number of vertices the g emits
     * may be less than the number of ids.
     */
    fun <V : Vertex> V(vararg ids: Any): List<V> = V(ids.toList())

    /**
     * Gets a graph g that emits vertices for given ids.
     * No exception is thrown for ids that don't correspond to a vertex, thus the number of vertices the g emits
     * may be less than the number of ids.
     */
    fun <V : Vertex> V(ids: Collection<Any>): List<V> =
            if (ids.none()) {
                g.inject<V>()
            } else {
                g.V(*ids.toTypedArray()).map { vertex ->
                    deserialize<V>(vertex.get())
                }
            }.toList()


    /**
     * Get a g that emits all vertices for class V (which has been registered as a vertex with this GraphMapper).
     * V may be a superclass of classes registered as a vertex.
     */
    fun <V : Vertex> V(
            kClass: KClass<V>,
            then: GraphTraversal<*, GraphVertex>.() -> GraphTraversal<*, GraphVertex> = { this }
    ): List<V> {
        val labels = graphDescription.vertexClasses.asSequence().filter { vertexKClass ->
            vertexKClass.isSubclassOf(kClass)
        }.map { vertexClass ->
            graphDescription.getVertexDescription(vertexClass).label
        }.toList()
        if (labels.isEmpty()) throw UnregisteredClass(kClass)
        logger.debug("Will get all vertices with labels $labels")
        return labels.asSequence().map { label ->
            g.V().hasLabel(label)
        }.reduce { traversal1, traversal2 ->
            g.V().union(traversal1, traversal2)
        }.then().map { vertex ->
            deserialize<V>(vertex.get())
        }.toList()
    }

    /**
     * Saves vertices to the graph. If the property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID. If the property annotated with @ID is non-null,
     * but the vertex cannot be found, an exception is thrown.
     */
    fun <V : Vertex> saveV(vararg vertices: V): List<V> = vertices.map { saveV(it) }

    /**
     * Saves vertices to the graph. If the property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID. If the property annotated with @ID is non-null,
     * but the vertex cannot be found, an exception is thrown.
     */
    fun <V : Vertex> saveV(vertices: Iterable<V>): List<V> = vertices.map { saveV(it) }

    /**
     * Saves an object annotated with @Element to the graph. If property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID. If the property annotated with @ID is non-null,
     * but the vertex cannot be found, an exception is thrown.
     */
    fun <V : Vertex> saveV(vertex: V): V {
        val serialized = serialize(vertex)
        logger.debug("Saved ${serialized.label()} vertex with id: ${serialized.id()}\n")
        return deserialize(serialized)
    }

    /**
     *
     *  Edges
     *
     */

    /**
     * Gets a graph g that emits an edge with the given id.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(id: Any): E? = E<FROM, TO, E>(listOf(id)).singleOrNull()

    /**
     * Gets a graph g that emits edges for given ids.
     * No exception is thrown for ids that don't correspond to an edge, thus the number of edges the g emits
     * may be less than the number of ids.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(vararg ids: Any): List<E> = E(ids.toList())

    /**
     * Gets a graph g that emits edges for given ids.
     * No exception is thrown for ids that don't correspond to an edge, thus the number of edges the g emits
     * may be less than the number of ids.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(ids: Collection<Any>): List<E> =
            if (ids.none()) {
                g.inject<E>()
            } else {
                g.E(*ids.toTypedArray()).map { edge ->
                    deserialize<FROM, TO, E>(edge.get())
                }
            }.toList()

    /**
     * Get a g that emits all edges for class E (which has been registered with a spec as
     * an edge with this GraphMapper).
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(
            kClass: KClass<E>,
            then: GraphTraversal<*, GraphEdge>.() -> GraphTraversal<*, GraphEdge> = { this }
    ): List<E> {
        val labels = graphDescription.edgeClasses.asSequence().filter { edgeKClass ->
            edgeKClass.isSubclassOf(kClass)
        }.map { vertexClass ->
            graphDescription.getEdgeDescription(vertexClass).label
        }.toList()
        if (labels.isEmpty()) throw UnregisteredClass(kClass)
        logger.debug("Will get all edges with labels $labels")
        return labels.asSequence().map { label ->
            g.E().hasLabel(label)
        }.reduce { traversal1, traversal2 ->
            g.E().union(traversal1, traversal2)
        }.then().map { edge ->
            deserialize<FROM, TO, E>(edge.get())
        }.toList()
    }

    /**
     * Saves edges to the graph. If the property annotated with @ID is null,
     * a new edge will be created, otherwise this object will overwrite the current edge with that id.
     * The returned object will always have a non-null @ID.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> saveE(vararg edges: E): List<E> = edges.map { saveE(it) }

    /**
     * Saves edges to the graph. If the property annotated with @ID is null,
     * a new edge will be created, otherwise this object will overwrite the current edge with that id.
     * The returned object will always have a non-null @ID.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> saveE(edges: Iterable<E>): List<E> = edges.map { saveE(it) }

    /**
     * Saves edges to the graph. If the property annotated with @ID is null,
     * a new edge will be created, otherwise this object will overwrite the current edge with that id.
     * The returned object will always have a non-null @ID.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> saveE(edge: E): E {
        val serialized = serialize(edge)
        logger.debug("Saved ${serialized.label()} edge with id ${serialized.id()}")
        return deserialize(serialized)
    }

    /**
     *
     * Traversals
     *
     */

    /**
     * Traverses from a vertex to the path's required destination object.
     */
    fun <FROM, TO> traverse(boundStep: SingleBoundStep.ToSingle<FROM, TO>): TO =
            traversal(boundStep).map { traverser -> traverser.get().second }.toList().single()

    /**
     * Traverses from a vertex to the path's optional destination object.
     */
    fun <FROM, TO> traverse(boundStep: SingleBoundStep.ToOptional<FROM, TO>): TO? =
            traversal(boundStep).map { traverser -> traverser.get().second }.toList().singleOrNull()

    /**
     * Traverses from vertex to the path's destination objects.
     */
    fun <FROM, TO> traverse(boundStep: SingleBoundStep.ToMany<FROM, TO>): List<TO> =
            traversal(boundStep).map { traverser -> traverser.get().second }.toList()

    /**
     * Traverses from multiple vertices to the path's required destination object for each origin vertex.
     */
    fun <FROM, TO> traverse(boundStep: BoundStep.ToSingle<FROM, TO>): Map<FROM, TO> =
            traversal(boundStep).toSingleMap(boundStep.froms)

    /**
     * Traverses from multiple vertices to the path's optional destination object for each origin vertex.
     */
    fun <FROM, TO> traverse(boundStep: BoundStep.ToOptional<FROM, TO>): Map<FROM, TO?> =
            traversal(boundStep).toOptionalMap(boundStep.froms)

    /**
     * Traverses from multiple vertices to the path's destination objects for each origin vertex.
     */
    fun <FROM, TO> traverse(boundStep: BoundStep.ToMany<FROM, TO>): Map<FROM, List<TO>> =
            traversal(boundStep).toMultiMap(boundStep.froms)

    /**
     * Traverses from any number of vertices to the path's destination object(s) for each origin vertex.
     */
    fun <FROM, TO> traversal(boundStep: BoundStep<FROM, TO>): GraphTraversal<*, Pair<FROM, TO>> =
            if (boundStep.froms.none()) {
                g.inject<Pair<FROM, TO>>()
            } else {
                val traversalStart = boundStep.froms.fold(initial = g.inject<FROM>()) { traversal, from ->
                    traversal.inject(from).`as`(fromKey)
                }
                val traversed = boundStep.step.invoke(StepTraverser(traversalStart, this))
                traversed.`as`(toKey).select<Any>(fromKey, toKey).map {
                    val map = it.get()
                    @Suppress("UNCHECKED_CAST")
                    val from = map[fromKey] as FROM
                    @Suppress("UNCHECKED_CAST")
                    val to = map[toKey] as TO
                    logger.debug("Traversed from $from to $to")
                    from to to
                }
            }

    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> serialize(edge: E): GraphEdge =
            EdgeSerializer(graphDescription, g)(edge)

    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> deserialize(graphEdge: GraphEdge): E =
            EdgeDeserializer(graphDescription)(graphEdge)

    fun <V : Vertex> serialize(vertex: V): GraphVertex =
            VertexSerializer(graphDescription, g)(vertex)

    fun <V : Vertex> deserialize(graphVertex: GraphVertex): V =
            VertexDeserializer(graphDescription)(graphVertex)

    fun <V : Vertex> vertexID(vertex: V): Any? {
        if (graphDescription.vertexClasses.contains(vertex::class)) {
            val vertexDescription = graphDescription.getVertexDescription(vertex::class)
            return vertexDescription.id.property.get(vertex)
        }
        return null
    }

    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> edgeID(edge: E): Any? {
        if (graphDescription.edgeClasses.contains(edge::class)) {
            val edgeDescription = graphDescription.getEdgeDescription(edge::class)
            return edgeDescription.id.property.get(edge)
        }
        return null
    }

    companion object {

        private const val fromKey = "from"

        private const val toKey = "to"

        private val logger = LoggerFactory.getLogger(GraphMapper::class.java)
    }
}

typealias GraphVertex = org.apache.tinkerpop.gremlin.structure.Vertex

typealias GraphEdge = org.apache.tinkerpop.gremlin.structure.Edge

/**
 * Get a g that emits all vertices for class V (which has been registered as a vertex with this GraphMapper).
 * V may be a superclass of classes registered as a vertex.
 */
inline fun <reified V : Vertex> GraphMapper.allV(
        noinline then: GraphTraversal<*, GraphVertex>.() -> GraphTraversal<*, GraphVertex> = { this }
): List<V> = V(V::class, then)

/**
 * Get a g that emits all edges for class E (which has been registered with a spec as
 * an edge with this GraphMapper).
 */
inline fun <FROM : Vertex, TO : Vertex, reified E : Edge<FROM, TO>> GraphMapper.allE(
        noinline then: GraphTraversal<*, GraphEdge>.() -> GraphTraversal<*, GraphEdge> = { this }
): List<E> = E(E::class, then)
