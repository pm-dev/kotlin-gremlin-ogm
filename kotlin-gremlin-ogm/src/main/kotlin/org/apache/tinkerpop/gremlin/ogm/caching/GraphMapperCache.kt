package org.apache.tinkerpop.gremlin.ogm.caching

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex

/**
 * The cache used by a CachedGraphMapper. The implementation should be thread-safe if a
 * CachedGraphMapper using this cache is shared across threads.
 */
interface GraphMapperCache {

    /**
     * Requests that the vertex be cached for the given id as its key.
     * This is called after a vertex is read (aka fetched, aka deserialized) from the graph.
     */
    fun <V : Vertex> putV(id: Any, vertex: V)

    /**
     * Checks to see if there is a cached vertex for a given id.
     */
    fun <V : Vertex> getV(id: Any): V?

    /**
     * Removes the vertex from the cache for a given id, if it exists.
     */
    fun invalidateV(id: Any)

    /**
     * Requests that the edge be cached for the given id as its key.
     * This is called after an edge is read (aka fetched, aka deserialized) from the graph.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> putE(id: Any, edge: E)

    /**
     * Checks to see if there is a cached edge for a given id.
     */
    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> getE(id: Any): E?

    /**
     * Removes the edge from the cache for a given id, if it exists.
     */
    fun invalidateE(id: Any)
}
