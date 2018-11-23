package org.apache.tinkerpop.gremlin.ogm.caching

import org.apache.tinkerpop.gremlin.ogm.GraphEdge
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.GraphVertex
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.slf4j.LoggerFactory

/**
 * A version of [GraphMapper] which supports caching of vertices and edges. The [GraphMapperCache] will be
 * invalidated for a Vertex or Edge whenever that Vertex or Edge is written (aka saved, aka serialized) to the graph.
 * Similarly, Edges/Vertices are written to the cache whenever they're read (aka fetched, aka deserialized) from the graph.
 *
 * **IMPORTANT** If a GraphMapper instance is shared across threads, the cache should be thread-safe.
 */
interface CachedGraphMapper : GraphMapper {

    val cache: GraphMapperCache

    fun <V : Vertex> load(graphVertex: GraphVertex): V =
            super.deserialize<V>(graphVertex).apply {
                logger.debug("Deserialized vertex with id ${graphVertex.id()}")
            }

    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> load(graphEdge: GraphEdge): E =
            super.deserialize<FROM, TO, E>(graphEdge).apply {
                logger.debug("Deserialized edge with id ${graphEdge.id()}")
            }

    override fun <V : Vertex> serialize(vertex: V): GraphVertex =
            super.serialize(vertex).also { serialized ->
                logger.debug("Serialized vertex with id ${serialized.id()}, will update cache.")
                val deserialized = if (vertexID(vertex) == null) super.deserialize(serialized) else vertex
                cache.put(serialized, deserialized)
            }

    override fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> serialize(edge: E): GraphEdge =
            super.serialize(edge).also { serialized ->
                logger.debug("Serialized edge with id ${serialized.id()}, will update cache.")
                val deserialized = if (edgeID(edge) == null) super.deserialize(serialized) else edge
                cache.put(serialized, deserialized)
            }

    override fun <V : Vertex> deserialize(graphVertex: GraphVertex): V =
            cache.get(graphVertex)

    override fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> deserialize(graphEdge: GraphEdge): E =
            cache.get(graphEdge)

    companion object {
        private val logger = LoggerFactory.getLogger(CachedGraphMapper::class.java)
    }
}
