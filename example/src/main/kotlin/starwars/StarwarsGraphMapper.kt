package starwars

import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.janusgraph.core.JanusGraphFactory
import org.springframework.stereotype.Component
import starwars.models.*
import starwars.models.Character.Companion.friends
import starwars.models.Sibling.Companion.siblings

@Component
internal class StarwarsGraphMapper : GraphMapper(
        g = graph.traversal(),
        vertices = setOf(
                Human::class,
                Droid::class),
        relationships = mapOf(
                friends to null,
                siblings to Sibling::class
        ),
        nestedObjects = setOf(
                Name::class
        ),
        scalarMappers = mapOf(
                Episode::class to Episode
        ))

private val graph = JanusGraphFactory.build()
        .set("storage.backend", "inmemory")
        .open()

