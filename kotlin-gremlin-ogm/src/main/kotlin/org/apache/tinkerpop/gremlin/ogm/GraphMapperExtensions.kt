@file:Suppress("unused")

package org.apache.tinkerpop.gremlin.ogm

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.bound.BoundPathToMany
import org.apache.tinkerpop.gremlin.ogm.paths.bound.BoundPathToOptional
import org.apache.tinkerpop.gremlin.ogm.paths.bound.BoundPathToSingle
import org.apache.tinkerpop.gremlin.ogm.paths.bound.SingleBoundPath
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 *
 *  ===== Vertices =====
 *
 */

/**
 * Fetch all vertices for class V (which has been registered as a vertex with this GraphMapper).
 * For this function, V may be a superclass of classes registered as a vertex.
 */
inline fun <reified V : Vertex> GraphMapper.fetchV(): List<V> = V(V::class).toList()

/**
 * Get a traversal that emits all vertices for class V (which has been registered as a vertex with this GraphMapper).
 * V may be a superclass of classes registered as a vertex.
 */
inline fun <reified V : Vertex> GraphMapper.V(): GraphTraversal<*, V> = V(V::class)

/**
 * Fetches a vertex with a given id.
 */
fun <V : Vertex> GraphMapper.fetchV(id: Any): V? = fetchV<V>(listOf(id)).singleOrNull()

/**
 * Gets a graph traversal that emits a vertex with a given id.
 */
fun <V : Vertex> GraphMapper.V(id: Any): GraphTraversal<*, V> = V(listOf(id))

/**
 * Fetches vertices for given ids.
 * No exception is thrown for ids that don't correspond to a vertex, thus the returned list
 * may be less than the number of ids.
 */
fun <V : Vertex> GraphMapper.fetchV(vararg ids: Any): List<V> = fetchV(ids.asList())

/**
 * Gets a graph traversal that emits vertices for given ids.
 * No exception is thrown for ids that don't correspond to a vertex, thus the number of vertices the traversal emits
 * may be less than the number of ids.
 */
fun <V : Vertex> GraphMapper.V(vararg ids: Any): GraphTraversal<*, V> = V(ids.toList())

/**
 * Fetches vertices for given ids.
 * No exception is thrown for ids that don't correspond to a vertex, thus the returned list
 * may be less than the number of ids.
 */
fun <V : Vertex> GraphMapper.fetchV(ids: Collection<Any>): List<V> = V<V>(ids).toList()

/**
 * Saves vertices to the graph. If the property annotated with @ID is null,
 * a new vertex will be created, otherwise this object will overwrite the current vertex with that id.
 * The returned object will always have a non-null @ID. If the property annotated with @ID is non-null,
 * but the vertex cannot be found, an exception is thrown.
 */
fun <V : Vertex> GraphMapper.saveV(vararg objs: V): List<V> = objs.map { saveV(it) }

/**
 *
 *  ===== Edges =====
 *
 */

/**
 * Fetch all edges for class E (which has been registered with a relationship as
 * an edge with this GraphMapper).
 */
inline fun <FROM : Vertex, TO : Vertex, reified E : Edge<FROM, TO>> GraphMapper.fetchE(): List<E> = E(E::class).toList()

/**
 * Get a traversal that emits all edges for class E (which has been registered with a relationship as
 * an edge with this GraphMapper).
 */
inline fun <FROM : Vertex, TO : Vertex, reified E : Edge<FROM, TO>> GraphMapper.E(): GraphTraversal<*, E> = E(E::class)

/**
 * Fetches an edge with a given id. Null is returned if no edge with the given id exists in the graph.
 */
fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> GraphMapper.fetchE(id: Any): E? = fetchE<FROM, TO, E>(listOf(id)).singleOrNull()

/**
 * Gets a graph traversal that emits an edge with the given id.
 */
fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> GraphMapper.E(id: Any): GraphTraversal<*, E> = E(listOf(id))

/**
 * Fetches edges for given ids.
 * No exception is thrown for ids that don't correspond to an edge, thus the number of edges the traversal emits
 * may be less than the number of ids.
 */
fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> GraphMapper.fetchE(vararg ids: Any): List<E> = fetchE(ids.asList())

/**
 * Gets a graph traversal that emits edges for given ids.
 * No exception is thrown for ids that don't correspond to an edge, thus the number of edges the traversal emits
 * may be less than the number of ids.
 */
fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> GraphMapper.E(vararg ids: Any): GraphTraversal<*, E> = E(ids.asList())

/**
 * Fetches edges for given ids.
 * No exception is thrown for ids that don't correspond to an edge, thus the number of edges the traversal emits
 * may be less than the number of ids.
 */
fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> GraphMapper.fetchE(ids: Collection<Any>): List<E> = E<FROM, TO, E>(ids).toList()

/**
 * Saves edges to the graph. If the property annotated with @ID is null,
 * a new edge will be created, otherwise this object will overwrite the current edge with that id.
 * The returned object will always have a non-null @ID.
 */
fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> GraphMapper.saveE(vararg edges: E): List<E> = edges.map { saveE(it) }

/**
 * Saves edges to the graph. If the property annotated with @ID is null,
 * a new edge will be created, otherwise this object will overwrite the current edge with that id.
 * The returned object will always have a non-null @ID.
 */
fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> GraphMapper.saveE(edges: Iterable<E>): List<E> = edges.map { saveE(it) }

/**
 *
 * Traversals
 *
 */

/**
 * Traverses from a vertex to the path's required destination object.
 */
fun <FROM : Vertex, TO> GraphMapper.traverse(boundStep: SingleBoundPath.ToSingle<FROM, TO>): TO =
        traverse(boundStep.froms, boundStep.path)[boundStep.from]!!.single()

/**
 * Traverses from a vertex to the path's optional destination object.
 */
fun <FROM : Vertex, TO> GraphMapper.traverse(boundStep: SingleBoundPath.ToOptional<FROM, TO>): TO? =
        traverse(boundStep.froms, boundStep.path)[boundStep.from]!!.singleOrNull()

/**
 * Traverses from vertex to the path's destination objects.
 */
fun <FROM : Vertex, TO> GraphMapper.traverse(boundStep: SingleBoundPath.ToMany<FROM, TO>): List<TO> =
        traverse(boundStep.froms, boundStep.path)[boundStep.from]!!

/**
 * Traverses from multiple vertices to the path's required destination object for each origin vertex.
 */
fun <FROM : Vertex, TO> GraphMapper.traverse(boundStep: BoundPathToSingle<FROM, TO>): Map<FROM, TO> =
        traverse(boundStep.froms, boundStep.path).entries.associate { it.key to it.value.single() }

/**
 * Traverses from multiple vertices to the path's optional destination object for each origin vertex.
 */
fun <FROM : Vertex, TO> GraphMapper.traverse(boundStep: BoundPathToOptional<FROM, TO>): Map<FROM, TO?> =
        traverse(boundStep.froms, boundStep.path).entries.associate { it.key to it.value.singleOrNull() }

/**
 * Traverses from multiple vertices to the path's destination object for each origin vertex.
 */
fun <FROM : Vertex, TO> GraphMapper.traverse(boundStep: BoundPathToMany<FROM, TO>): Map<FROM, List<TO>> =
        traverse(boundStep.froms, boundStep.path)
