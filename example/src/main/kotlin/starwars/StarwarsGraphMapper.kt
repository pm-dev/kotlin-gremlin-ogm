package starwars

import org.apache.tinkerpop.gremlin.ogm.caching.CachedGraphMapper
import org.apache.tinkerpop.gremlin.ogm.caching.GraphMapperCache
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Graph
import org.springframework.stereotype.Component
import java.util.function.Supplier

@Component
internal open class StarwarsGraphMapper(
        graphSupplier: Supplier<Graph>,
        override val graphDescription: GraphDescription,
        override val cache: GraphMapperCache
) : CachedGraphMapper {

    private val graph = graphSupplier.get()

    override val g: GraphTraversalSource get() = graph.traversal()
}
