package org.apache.tinkerpop.gremlin.ogm.caching

import org.apache.tinkerpop.gremlin.ogm.GraphEdge
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.GraphVertex
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex

/**
 * A version of [GraphMapper] which supports caching of vertices and edges. The [GraphMapperCache] will be
 * invalidated for a Vertex or Edge whenever that Vertex or Edge is written (aka saved, aka serialized) to the graph.
 * Similarly, Edges/Vertices are written to the cache whenever they're read (aka fetched, aka deserialized) from the graph.
 *
 * **IMPORTANT** If a GraphMapper instance is shared across threads, the cache should be thread-safe.
 */
interface CachedGraphMapper : GraphMapper {

    val cache: GraphMapperCache

    override fun <V : Vertex> serializeV(vertex: V): GraphVertex =
        cache.invalidateV(vertex).run {
            super.serializeV(vertex)
        }

    override fun <V : Vertex> deserializeV(graphVertex: GraphVertex): V =
            cache.getV(graphVertex.id()) ?: run {
                val deserialized = super.deserializeV<V>(graphVertex)
                cache.putV(graphVertex.id(), deserialized)
                deserialized
            }

    override fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> serializeE(edge: E): GraphEdge =
            cache.invalidateE(edge).run {
                super.serializeE(edge)
            }

    override fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> deserializeE(graphEdge: GraphEdge): E =
            cache.getE(graphEdge.id()) ?: run {
                val deserialized = super.deserializeE<FROM, TO, E>(graphEdge)
                cache.putE(graphEdge.id(), deserialized)
                deserialized
            }
}
