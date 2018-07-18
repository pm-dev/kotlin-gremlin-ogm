package starwars

import com.google.common.cache.CacheBuilder
import org.apache.tinkerpop.gremlin.ogm.caching.GraphMapperCache
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
internal class StarwarsGraphCache : GraphMapperCache {

    private val cache = CacheBuilder.newBuilder().build<Any, Any>()

    @Suppress("UNCHECKED_CAST")
    override fun <V : Vertex> getV(id: Any): V? =
        cache.getIfPresent(id).apply {
            logger.debug("Cache ${if (this == null) "miss" else "hit"} for vertex with id $id")
        } as? V

    @Suppress("UNCHECKED_CAST")
    override fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> getE(id: Any): E? =
            cache.getIfPresent(id).apply {
                logger.debug("Cache ${if (this == null) "miss" else "hit"} for edge with id $id")
            } as? E

    override fun <V : Vertex> putV(id: Any, vertex: V) = cache.put(id, vertex)

    override fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> putE(id: Any, edge: E) = cache.put(id, edge)

    override fun invalidateV(id: Any) = cache.invalidate(id)

    override fun invalidateE(id: Any) = cache.invalidate(id)

    companion object {
        private val logger = LoggerFactory.getLogger(StarwarsGraphCache::class.java)
    }
}
