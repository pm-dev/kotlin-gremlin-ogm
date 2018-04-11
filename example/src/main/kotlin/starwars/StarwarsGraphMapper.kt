package starwars

import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.springframework.stereotype.Component
import starwars.models.Droid
import starwars.models.Episode
import starwars.models.Human
import starwars.models.Name

@Component
internal class StarwarsGraphMapper : GraphMapper(
        g = TinkerGraph.open().traversal(),
        vertexClasses = setOf(
                Human::class,
                Droid::class),
        nestedObjectClasses = setOf(
                Name::class
        ),
        scalarMappers = mapOf(
                Episode::class to Episode
        ))
