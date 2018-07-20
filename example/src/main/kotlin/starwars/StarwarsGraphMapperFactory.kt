package starwars

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import graphql.servlet.batched.GraphMapperFactory
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

@Component
internal class StarwarsGraphMapperFactory : GraphMapperFactory {

    private val graphDescription = CachedGraphDescription(

            // 1) Additional vertices must be registered here.
            vertices = setOf(
                    Human::class,
                    Droid::class),

            // 2) Additional relationships must be registered here. If the relationship
            // has a corresponding Edge, add that as the map entry's value (otherwise, null).
            relationships = mapOf(
                    Character.friends to null,
                    Sibling.siblings to Sibling::class
            ),

            // 3) Additional objects (that are not vertices or edges), that may be
            // persisted to the graph via a property of an edge or vertex must be
            // registered here.
            objectProperties = setOf(
                    Name::class
            ),

            // 4) Additional objects that should be persisted to the graph as a single
            // value must be registered here. The map entry's value is a 'PropertyBiMapper' which knows how to
            // serlialize/deserialize the object to/from the graph. CachedGraphDescription provides
            // some supported scalar properties by default
            scalarProperties = mapOf(
                    Episode::class to Episode
            ))

    private val traversal = JanusGraphFactory.build()
            .set("storage.backend", "inmemory")
            .set("index.search.backend", "lucene")
            .set("index.search.directory", "/tmp")
            .open().apply {
                IndicesBuilder(graphDescription).invoke(this)
            }.traversal()

    override operator fun invoke() = object : CachedGraphMapper {

        override val graphDescription: GraphDescription get() = this@StarwarsGraphMapperFactory.graphDescription

        override val traversal: GraphTraversalSource get() = this@StarwarsGraphMapperFactory.traversal

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
        }
    }

    private val logger = LoggerFactory.getLogger(StarwarsGraphMapperFactory::class.java)

    private class IndicesBuilder(override val graphDescription: GraphDescription) : JanusGraphIndicesBuilder
}
