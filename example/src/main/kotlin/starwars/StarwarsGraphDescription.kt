package starwars

import org.apache.tinkerpop.gremlin.ogm.reflection.CachedGraphDescription
import org.springframework.stereotype.Component
import starwars.models.*

@Component
internal class StarwarsGraphDescription : CachedGraphDescription(
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
