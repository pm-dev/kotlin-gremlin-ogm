@file:Suppress("unused")

package org.apache.tinkerpop.gremlin.ogm

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.UnregisteredClass
import org.apache.tinkerpop.gremlin.ogm.mappers.EdgeDeserializer
import org.apache.tinkerpop.gremlin.ogm.mappers.EdgeSerializer
import org.apache.tinkerpop.gremlin.ogm.mappers.VertexDeserializer
import org.apache.tinkerpop.gremlin.ogm.mappers.VertexSerializer
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.bound.BoundPath
import org.apache.tinkerpop.gremlin.ogm.paths.bound.SingleBoundPath
import org.apache.tinkerpop.gremlin.ogm.paths.steps.StepTraverser
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.ogm.traversals.*
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

    val traversal: GraphTraversalSource

    /**
     *
     * Vertices
     *
     */

    /**
     * Gets a graph traversal that emits a vertex with a given id.
     */
    fun <V : Vertex> V(id: Any): GraphTraversalToOptional<V> = V<V>(setOf(id)).asToOptional()

    /**
     * Gets a graph traversal that emits vertices for given ids.
     * No exception is thrown for ids that don't correspond to a vertex, thus the number of vertices the traversal emits
     * may be less than the number of ids.
     */
    fun <V : Vertex> V(vararg ids: Any): GraphTraversalToMany<V> = V(ids.toSet())

    /**
     * Gets a graph traversal that emits vertices for given ids.
     * No exception is thrown for ids that don't correspond to a vertex, thus the number of vertices the traversal emits
     * may be less than the number of ids.
     */
    fun <V : Vertex> V(ids: Set<Any>) = GraphTraversalToMany(
            if (ids.none()) {
                traversal.inject<V>()
            } else {
                traversal.V(*ids.toTypedArray()).map { vertex ->
                    deserialize<V>(vertex.get())
                }
            }
    )


    /**
     * Get a traversal that emits all vertices for class V (which has been registered as a vertex with this GraphMapper).
     * V may be a superclass of classes registered as a vertex.
     */
    fun <V : Vertex> V(
            kClass: KClass<V>,
            then: GraphTraversal<*, GraphVertex>.() -> GraphTraversal<*, GraphVertex> = { this }
    ): GraphTraversalToMany<V> {
        val labels = graphDescription.vertexClasses.asSequence().filter { vertexKClass ->
            vertexKClass.isSubclassOf(kClass)
        }.map { vertexClass ->
            graphDescription.getVertexDescription(vertexClass).label
        }.toList()
        if (labels.isEmpty()) throw UnregisteredClass(kClass)
        logger.debug("Will get all vertices with labels $labels")
        val traversal = labels.asSequence().map { label ->
            traversal.V().hasLabel(label)
        }.reduce { traversal1, traversal2 ->
            traversal.V().union(traversal1, traversal2)
        }.then().map { vertex ->
            deserialize<V>(vertex.get())
        }
        return GraphTraversalToMany(traversal)
    }

    /**
     * Saves vertices to the graph. If the property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID. If the property annotated with @ID is non-null,
     * but the vertex cannot be found, an exception is thrown.
     */
    fun <V : Vertex> saveV(vararg objs: V): List<V> = objs.map { saveV(it) }

    /**
     * Saves vertices to the graph. If the property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID. If the property annotated with @ID is non-null,
     * but the vertex cannot be found, an exception is thrown.
     */
    fun <V : Vertex> saveV(objs: Iterable<V>): List<V> = objs.map { saveV(it) }

    /**
     * Saves an object annotated with @Element to the graph. If property annotated with @ID is null,
     * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
     * The returned object will always have a non-null @ID. If the property annotated with @ID is non-null,
     * but the vertex cannot be found, an exception is thrown.
     */
    fun <V : Vertex> saveV(deserialized: V): V {
        val serialized = serialize(deserialized)
        logger.debug("Saved ${serialized.label()} vertex with id: ${serialized.id()}\n")
        return deserialize(serialized)
    }

    /**
     *
     *  Edges
     *
     */

    /**
     * Gets a graph traversal that emits an edge with the given id.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(id: Any): GraphTraversalToOptional<E> = E<FROM, TO, E>(setOf(id)).asToOptional()

    /**
     * Gets a graph traversal that emits edges for given ids.
     * No exception is thrown for ids that don't correspond to an edge, thus the number of edges the traversal emits
     * may be less than the number of ids.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(vararg ids: Any): GraphTraversalToMany<E> = E(ids.toSet())

    /**
     * Gets a graph traversal that emits edges for given ids.
     * No exception is thrown for ids that don't correspond to an edge, thus the number of edges the traversal emits
     * may be less than the number of ids.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(ids: Set<Any>) = GraphTraversalToMany(
            if (ids.none()) {
                traversal.inject<E>()
            } else {
                traversal.E(*ids.toTypedArray()).map { edge ->
                    deserialize<FROM, TO, E>(edge.get())
                }
            }
    )

    /**
     * Get a traversal that emits all edges for class E (which has been registered with a relationship as
     * an edge with this GraphMapper).
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(
            kClass: KClass<E>,
            then: GraphTraversal<*, GraphEdge>.() -> GraphTraversal<*, GraphEdge> = { this }
    ): GraphTraversalToMany<E> {
        val labels = graphDescription.edgeClasses.asSequence().filter { edgeKClass ->
            edgeKClass.isSubclassOf(kClass)
        }.map { vertexClass ->
            graphDescription.getEdgeDescription(vertexClass).label
        }.toList()
        if (labels.isEmpty()) throw UnregisteredClass(kClass)
        logger.debug("Will get all edges with labels $labels")
        val traversal = labels.asSequence().map { label ->
            traversal.E().hasLabel(label)
        }.reduce { traversal1, traversal2 ->
            traversal.E().union(traversal1, traversal2)
        }.then().map { edge ->
            deserialize<FROM, TO, E>(edge.get())
        }
        return GraphTraversalToMany(traversal)
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
    fun <FROM : Vertex, TO> traverse(boundPath: SingleBoundPath.ToSingle<FROM, TO>): GraphTraversalToSingle<TO> =
            GraphTraversalToSingle(_traversal(boundPath).traversal.map { traverser -> traverser.get().second })

    /**
     * Traverses from a vertex to the path's optional destination object.
     */
    fun <FROM : Vertex, TO> traverse(boundPath: SingleBoundPath.ToOptional<FROM, TO>): GraphTraversalToOptional<TO> =
            GraphTraversalToOptional(_traversal(boundPath).traversal.map { traverser -> traverser.get().second })

    /**
     * Traverses from vertex to the path's destination objects.
     */
    fun <FROM : Vertex, TO> traversal(boundPath: SingleBoundPath.ToMany<FROM, TO>): GraphTraversalToMany<TO> =
            GraphTraversalToMany(_traversal(boundPath).traversal.map { traverser -> traverser.get().second })

    /**
     * Traverses from multiple vertices to the path's required destination object for each origin vertex.
     */
    fun <FROM : Vertex, TO> traversal(boundPath: BoundPath.ToSingle<FROM, TO>): MultiBoundGraphTraversalToSingle<FROM, TO> =
            _traversal(boundPath).asToSingle()

    /**
     * Traverses from multiple vertices to the path's optional destination object for each origin vertex.
     */
    fun <FROM : Vertex, TO> traversal(boundPath: BoundPath.ToOptional<FROM, TO>): MultiBoundGraphTraversalToOptional<FROM, TO> =
            _traversal(boundPath).asToOptional()

    /**
     * Traverses from multiple vertices to the path's destination objects for each origin vertex.
     */
    fun <FROM : Vertex, TO> traversal(boundPath: BoundPath.ToMany<FROM, TO>): MultiBoundGraphTraversalToMany<FROM, TO> =
            _traversal(boundPath)

    /**
     * Traverses from any number of vertices to the path's destination object(s) for each origin vertex.
     */
    private fun <FROM : Vertex, TO> _traversal(boundPath: BoundPath<FROM, TO>) = MultiBoundGraphTraversalToMany(
            froms = boundPath.froms,
            traversal =
            if (boundPath.froms.none()) {
                traversal.inject<Pair<FROM, TO>>()
            } else {
                val traversalStart = boundPath.froms.fold(initial = traversal.inject<FROM>()) { traversal, from ->
                    traversal.inject(from).`as`(fromKey)
                }
                @Suppress("UNCHECKED_CAST")
                val traversed = boundPath.path.path().fold(initial = traversalStart as GraphTraversal<Any, Any>) { traversal, step ->
                    step as Path<Any, Any>
                    step(StepTraverser(traversal, this)) as GraphTraversal<Any, Any>
                }
                @Suppress("UNCHECKED_CAST")
                traversed.`as`(toKey).select<Any>(fromKey, toKey).map {
                    val map = it.get()
                    val from = map[fromKey] as FROM
                    val to = map[toKey] as TO
                    logger.debug("Traversed from $from to $to")
                    from to to
                }
            })

    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> serialize(edge: E): GraphEdge =
            EdgeSerializer(graphDescription, traversal)(edge)

    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> deserialize(graphEdge: GraphEdge): E =
            EdgeDeserializer(graphDescription)(graphEdge)

    fun <V : Vertex> serialize(vertex: V): GraphVertex =
            VertexSerializer(graphDescription, traversal)(vertex)

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
 * Get a traversal that emits all vertices for class V (which has been registered as a vertex with this GraphMapper).
 * V may be a superclass of classes registered as a vertex.
 */
inline fun <reified V : Vertex> GraphMapper.allV(
        noinline then: GraphTraversal<*, GraphVertex>.() -> GraphTraversal<*, GraphVertex> = { this }
): GraphTraversalToMany<V> = V(V::class, then)

/**
 * Get a traversal that emits all edges for class E (which has been registered with a relationship as
 * an edge with this GraphMapper).
 */
inline fun <FROM : Vertex, TO : Vertex, reified E : Edge<FROM, TO>> GraphMapper.allE(
        noinline then: GraphTraversal<*, GraphEdge>.() -> GraphTraversal<*, GraphEdge> = { this }
): GraphTraversalToMany<E> = E(E::class, then)
