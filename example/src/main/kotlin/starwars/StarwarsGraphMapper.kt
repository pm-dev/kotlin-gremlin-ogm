package starwars

import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.janusgraph.core.JanusGraphFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import starwars.models.Droid
import starwars.models.Episode
import starwars.models.Human
import starwars.models.Name

@Component
internal class StarwarsGraphMapper : GraphMapper(
        g = graph.traversal(),
        vertexClasses = setOf(
                Human::class,
                Droid::class),
        nestedObjectClasses = setOf(
                Name::class
        ),
        scalarMappers = mapOf(
                Episode::class to Episode
        ))

private val graph = JanusGraphFactory.build()
        .set("storage.backend", "inmemory")
        .open()

