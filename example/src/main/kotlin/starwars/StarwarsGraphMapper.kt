package starwars

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import org.apache.tinkerpop.gremlin.ogm.GraphEdge
import org.apache.tinkerpop.gremlin.ogm.GraphVertex
import org.apache.tinkerpop.gremlin.ogm.caching.CachedGraphMapper
import org.apache.tinkerpop.gremlin.ogm.caching.GraphMapperCache
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

internal class StarwarsGraphMapper(
        override val graphDescription: GraphDescription,
        override val g: GraphTraversalSource
) : CachedGraphMapper {

    override val cache = object : GraphMapperCache {

        private val vertexCache: LoadingCache<GraphVertex, Vertex> = CacheBuilder.newBuilder().build(
                object : CacheLoader<GraphVertex, Vertex>() {
                    override fun load(key: GraphVertex): Vertex = this@StarwarsGraphMapper.load(key)
                })

        private val edgeCache: LoadingCache<GraphEdge, Edge<Any, Any>> = CacheBuilder.newBuilder().build(
                object : CacheLoader<GraphEdge, Edge<Any, Any>>() {
                    override fun load(key: GraphEdge): Edge<Any, Any> = this@StarwarsGraphMapper.load(key)
                })

        @Suppress("UNCHECKED_CAST")
        override fun <V : Vertex> get(serialized: GraphVertex): V =
                vertexCache.get(serialized) as V

        @Suppress("UNCHECKED_CAST")
        override fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> get(serialized: GraphEdge): E =
                edgeCache.get(serialized) as E

        override fun <V : Vertex> put(serialized: GraphVertex, deserialized: V) =
                vertexCache.put(serialized, deserialized)

        override fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> put(serialized: GraphEdge, deserialized: E) =
                edgeCache.put(serialized, deserialized)
    }
}
