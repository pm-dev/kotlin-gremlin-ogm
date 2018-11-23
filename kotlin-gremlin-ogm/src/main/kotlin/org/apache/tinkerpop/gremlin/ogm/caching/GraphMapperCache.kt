package org.apache.tinkerpop.gremlin.ogm.caching

import org.apache.tinkerpop.gremlin.ogm.GraphEdge
import org.apache.tinkerpop.gremlin.ogm.GraphVertex
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex

/**
 * The cache used by a CachedGraphMapper. The implementation should be thread-safe if a
 * CachedGraphMapper using this cache is shared across threads.
 */
interface GraphMapperCache {

    /**
     * Requests that the vertex be explicitly cached using its serialized graph form as the key.
     */
    fun <V : Vertex> put(serialized: GraphVertex, deserialized: V)

    /**
     * Requests a vertex from the cache for a given serialized vertex. If there is a cache miss, implementors
     * should call CachedGraphMapper#load
     */
    fun <V : Vertex> get(serialized: GraphVertex): V

    /**
     * Requests that the edge be explicitly cached using its serialized graph form as the key.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> put(serialized: GraphEdge, deserialized: E)

    /**
     * Requests an edge from the cache for a given serialized edge. If there is a cache miss, implementors
     * should call CachedGraphMapper#load
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> get(serialized: GraphEdge): E
}
