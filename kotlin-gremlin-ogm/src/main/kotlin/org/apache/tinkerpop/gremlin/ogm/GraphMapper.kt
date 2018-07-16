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
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.bound.BoundPathToMany
import org.apache.tinkerpop.gremlin.ogm.paths.bound.BoundPathToOptional
import org.apache.tinkerpop.gremlin.ogm.paths.bound.BoundPathToSingle
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
     * Gets a graph traversal that emits a vertex with a given id.
     */
    fun <V : Vertex> V(id: Any): GraphTraversalToOptional<*, V> = V<V>(setOf(id)).toOptional()

    /**
     * Gets a graph traversal that emits vertices for given ids.
     * No exception is thrown for ids that don't correspond to a vertex, thus the number of vertices the traversal emits
     * may be less than the number of ids.
     */
    fun <V : Vertex> V(vararg ids: Any): GraphTraversalToMany<*, V> = V(ids.toSet())

    /**
     * Gets a graph traversal that emits vertices for given ids.
     * No exception is thrown for ids that don't correspond to a vertex, thus the number of vertices the traversal emits
     * may be less than the number of ids.
     */
    fun <V : Vertex> V(ids: Set<Any>): GraphTraversalToMany<*, V> {
        return if (ids.none()) {
            g.inject<V>()
        } else {
            g.V(*ids.toTypedArray()).map { vertex ->
                deserializeV<V>(vertex.get())
            }
        }.toMany()
    }


    /**
     * Get a traversal that emits all vertices for class V (which has been registered as a vertex with this GraphMapper).
     * V may be a superclass of classes registered as a vertex.
     */
    fun <V : Vertex> V(kClass: KClass<V>, then: GraphTraversal<*, GraphVertex>.() -> GraphTraversal<*, GraphVertex> = { this }): GraphTraversalToMany<*, V> {
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
        }.then().map { vertex ->
            deserializeV<V>(vertex.get())
        }.toMany()
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
        val serialized = serializeV(deserialized)
        logger.debug("Saved ${serialized.label()} vertex with id: ${serialized.id()}\n")
        return deserializeV(serialized)
    }

    /**
     *
     *  Edges
     *
     */

    /**
     * Gets a graph traversal that emits an edge with the given id.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(id: Any): GraphTraversalToOptional<*, E> = E<FROM, TO, E>(setOf(id)).toOptional()

    /**
     * Gets a graph traversal that emits edges for given ids.
     * No exception is thrown for ids that don't correspond to an edge, thus the number of edges the traversal emits
     * may be less than the number of ids.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(vararg ids: Any): GraphTraversalToMany<*, E> = E(ids.toSet())

    /**
     * Gets a graph traversal that emits edges for given ids.
     * No exception is thrown for ids that don't correspond to an edge, thus the number of edges the traversal emits
     * may be less than the number of ids.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(ids: Set<Any>): GraphTraversalToMany<*, E> {
        return if (ids.none()) {
            g.inject<E>()
        } else {
            g.E(*ids.toTypedArray()).map { edge ->
                deserializeE<FROM, TO, E>(edge.get())
            }
        }.toMany()
    }

    /**
     * Get a traversal that emits all edges for class E (which has been registered with a relationship as
     * an edge with this GraphMapper).
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> E(
            kClass: KClass<E>,
            then: GraphTraversal<*, GraphEdge>.() -> GraphTraversal<*, GraphEdge> = { this }
    ): GraphTraversalToMany<*, E> {
        val labels = graphDescription.edgeClasses.filter { edgeKClass ->
            edgeKClass.isSubclassOf(kClass)
        }.map { vertexClass ->
            graphDescription.getEdgeDescription(vertexClass).label
        }
        if (labels.isEmpty()) throw UnregisteredClass(kClass)
        logger.debug("Will get all edges with labels $labels")
        return labels.map { label ->
            g.E().hasLabel(label)
        }.reduce { traversal1, traversal2 ->
            g.E().union(traversal1, traversal2)
        }.then().map { edge ->
            deserializeE<FROM, TO, E>(edge.get())
        }.toMany()
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
        val serialized = serializeE(edge)
        logger.debug("Saved ${serialized.label()} edge with id ${serialized.id()}")
        return deserializeE(serialized)
    }

    /**
     *
     * Traversals
     *
     */

    /**
     * Traverses from a vertex to the path's required destination object.
     */
    fun <FROM : Vertex, TO> traverse(boundStep: SingleBoundPath.ToSingle<FROM, TO>): GraphTraversalToSingle<*, TO> =
            traverse(boundStep.froms, boundStep.path).traversal.map { it.get().second }.toSingle()

    /**
     * Traverses from a vertex to the path's optional destination object.
     */
    fun <FROM : Vertex, TO> traverse(boundStep: SingleBoundPath.ToOptional<FROM, TO>): GraphTraversalToOptional<*, TO> =
            traverse(boundStep.froms, boundStep.path).traversal.map { it.get().second }.toOptional()

    /**
     * Traverses from vertex to the path's destination objects.
     */
    fun <FROM : Vertex, TO> traverse(boundStep: SingleBoundPath.ToMany<FROM, TO>): GraphTraversalToMany<*, TO> =
            traverse(boundStep.froms, boundStep.path).traversal.map { it.get().second }.toMany()

    /**
     * Traverses from multiple vertices to the path's required destination object for each origin vertex.
     */
    fun <FROM : Vertex, TO> traverse(boundStep: BoundPathToSingle<FROM, TO>): Map<FROM, TO> =
            traverse(boundStep.froms, boundStep.path).toSingleMap(boundStep.froms)

    /**
     * Traverses from multiple vertices to the path's optional destination object for each origin vertex.
     */
    fun <FROM : Vertex, TO> traverse(boundStep: BoundPathToOptional<FROM, TO>): Map<FROM, TO?> =
            traverse(boundStep.froms, boundStep.path).toOptionalMap(boundStep.froms)

    /**
     * Traverses from multiple vertices to the path's destination object for each origin vertex.
     */
    fun <FROM : Vertex, TO> traverse(boundStep: BoundPathToMany<FROM, TO>): Map<FROM, List<TO>> =
            traverse(boundStep.froms, boundStep.path).toMultiMap(boundStep.froms)

    fun <FROM : Vertex, TO> traverse(froms: Iterable<FROM>, path: Path<FROM, TO>): GraphTraversalToMany<*, Pair<FROM, TO>> {
        if (froms.none()) {
            return g.inject<Pair<FROM, TO>>().toMany()
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
        return traversed.`as`(toKey).select<Any>(fromKey, toKey).map {
            val map = it.get()
            val from = map[fromKey] as FROM
            val to = map[toKey] as TO
            logger.debug("Traversed from $from to $to")
            from to to
        }.toMany()
    }

    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> deserializeE(edge: GraphEdge): E =
            EdgeDeserializer(graphDescription)(edge)

    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> serializeE(edge: E): GraphEdge =
            EdgeSerializer(graphDescription, g)(edge)

    fun <V : Vertex> serializeV(deserialized: V): GraphVertex =
            VertexSerializer(graphDescription, g)(deserialized)

    fun <V : Vertex> deserializeV(serialized: GraphVertex): V =
            VertexDeserializer(graphDescription)(serialized)

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
): GraphTraversalToMany<*, V> = V(V::class, then)

/**
 * Get a traversal that emits all edges for class E (which has been registered with a relationship as
 * an edge with this GraphMapper).
 */
inline fun <FROM : Vertex, TO : Vertex, reified E : Edge<FROM, TO>> GraphMapper.allE(
        noinline then: GraphTraversal<*, GraphEdge>.() -> GraphTraversal<*, GraphEdge> = { this }
): GraphTraversalToMany<*, E> = E(E::class, then)
