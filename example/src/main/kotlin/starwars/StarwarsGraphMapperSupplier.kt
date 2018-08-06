package starwars

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.caching.CachedGraphMapper
import org.apache.tinkerpop.gremlin.ogm.caching.GraphMapperCache
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.reflection.CachedGraphDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.janusgraph.core.JanusGraphFactory
import org.janusgraph.ogm.JanusGraphIndicesBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import starwars.models.*
import java.util.function.Supplier

@Component
internal class StarwarsGraphMapperSupplier : Supplier<GraphMapper> {

    private val cachedGraphDescription = CachedGraphDescription(

            // 1) All vertices must be registered here. Do not include abstract classes
            vertices = setOf(
                    Human::class,
                    Droid::class),

            // 2) All relationships must be registered here. If the relationship
            // has a corresponding Edge, add that as the map entry's value (otherwise, null).
            relationships = mapOf(
                    Character.friends to null,
                    Sibling.siblings to Sibling::class
            ),

            // 3) All objects (that are not vertices or edges), that may be
            // persisted to the graph via a property of an edge or vertex must be
            // registered here.
            objectProperties = setOf(
                    Name::class
            ),

            // 4) All objects that should be persisted to the graph as a single
            // value must be registered here. The map entry's value is a 'PropertyBiMapper' which knows how to
            // serlialize/deserialize the object to/from the graph. CachedGraphDescription provides
            // some supported scalar properties by default
            scalarProperties = mapOf(
                    Episode::class to Episode
            ))

    private val graph = JanusGraphFactory.build()
            .set("storage.backend", "inmemory")
            .set("index.search.backend", "lucene")
            .set("index.search.directory", "/tmp")
            .open().apply {
                IndicesBuilder(cachedGraphDescription).invoke(this)
            }

    override fun get(): GraphMapper = object : CachedGraphMapper {

        override val graphDescription: GraphDescription get() = cachedGraphDescription

        override val traversal: GraphTraversalSource get() = graph.traversal()

        override val cache = object : GraphMapperCache {

            private val backingCache: Cache<Any, Any> = CacheBuilder.newBuilder().build<Any, Any>()

            @Suppress("UNCHECKED_CAST")
            override fun <V : Vertex> getV(id: Any): V? =
                    backingCache.getIfPresent(id).apply {
                        logger.debug("Cache ${if (this == null) "miss" else "hit"} for vertex with id $id")
                    } as? V

            @Suppress("UNCHECKED_CAST")
            override fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> getE(id: Any): E? =
                    backingCache.getIfPresent(id).apply {
                        logger.debug("Cache ${if (this == null) "miss" else "hit"} for edge with id $id")
                    } as? E

            override fun <V : Vertex> putV(id: Any, vertex: V) = backingCache.put(id, vertex)

            override fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> putE(id: Any, edge: E) = backingCache.put(id, edge)

            override fun invalidateV(id: Any) = backingCache.invalidate(id)

            override fun invalidateE(id: Any) = backingCache.invalidate(id)

            private val logger = LoggerFactory.getLogger(StarwarsGraphMapperSupplier::class.java)
        }
    }

    private class IndicesBuilder(override val graphDescription: GraphDescription) : JanusGraphIndicesBuilder
}

