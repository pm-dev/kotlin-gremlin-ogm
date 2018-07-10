package starwars.graphql.character

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.apache.tinkerpop.gremlin.ogm.allV
import org.springframework.stereotype.Component
import starwars.StarwarsGraphMapper
import starwars.models.Character
import starwars.models.Name

@Component
internal class CharacterQueryResolver(
        private val gm: StarwarsGraphMapper
) : GraphQLQueryResolver {

    fun hero(): Character = character("Luke Skywalker")!!

    fun character(rawName: String): Character? {
        // JanusGraph will warn that this query requires iterating over all vertices.
        // This is because abstract Vertex classes are queried by union-ing queries of their base classes
        // Need to find a fix for this.
        val name = Name.parse(rawName)
        return gm.allV<Character> {
            has("name.first", name.first).apply {
                if (name.last != null) has("name.last", name.last)
            }
        }.toOptional().fetch()
    }
}

