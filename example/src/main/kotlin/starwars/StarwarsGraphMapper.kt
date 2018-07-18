package starwars

import org.apache.tinkerpop.gremlin.ogm.caching.CachedGraphMapper
import org.apache.tinkerpop.gremlin.ogm.caching.GraphMapperCache
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.springframework.stereotype.Component

@Component
internal open class StarwarsGraphMapper(
        override val g: GraphTraversalSource,
        override val graphDescription: GraphDescription,
        override val cache: GraphMapperCache
) : CachedGraphMapper
