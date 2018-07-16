package starwars

import org.apache.tinkerpop.gremlin.structure.Graph
import org.janusgraph.core.JanusGraphFactory
import org.janusgraph.ogm.JanusGraphIndicesBuilder
import org.springframework.stereotype.Component
import java.util.function.Supplier

@Component
internal class StarwarsGraphSupplier(private val graphIndicesBuilder: JanusGraphIndicesBuilder) : Supplier<Graph> {

    override fun get(): Graph {
        val graph = JanusGraphFactory.build()
                .set("storage.backend", "inmemory")
                .set("index.search.backend", "lucene")
                .set("index.search.directory", "/tmp")
                .open()
        graphIndicesBuilder(graph)
        return graph
    }
}
