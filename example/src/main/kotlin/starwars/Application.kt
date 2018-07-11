package starwars

import org.apache.tinkerpop.gremlin.ogm.reflection.CachedGraphDescription
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.janusgraph.core.JanusGraphFactory
import org.janusgraph.ogm.JanusGraphIndicesBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import starwars.models.*

@SpringBootApplication
internal open class Application {

    @Bean
    protected open fun graphTraversalSource(graphIndicesBuilder: JanusGraphIndicesBuilder): GraphTraversalSource {
        val graph = JanusGraphFactory.build()
                .set("storage.backend", "inmemory")
                .set("index.search.backend", "lucene")
                .set("index.search.directory", "/tmp")
                .open()
        graphIndicesBuilder(graph)
        return graph.traversal()
    }

    @Bean
    protected open fun graphDescription() = CachedGraphDescription(
            vertices = setOf(
                    Human::class,
                    Droid::class),
            relationships = mapOf(
                    Character.friends to null,
                    Sibling.siblings to Sibling::class
            ),
            objectProperties = setOf(
                    Name::class
            ),
            scalarProperties = mapOf(
                    Episode::class to Episode
            ))
}

internal fun main(args: Array<String>) {
    runApplication<Application>(*args)
}




