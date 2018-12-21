package starwars.graphql.character

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.ogm.graphMapper
import org.apache.tinkerpop.gremlin.ogm.allV
import org.springframework.stereotype.Component
import starwars.models.Character
import starwars.models.Name

@Component
internal class CharacterQueryResolver : GraphQLQueryResolver {

    fun hero(env: DataFetchingEnvironment): Character = character("Luke Skywalker", env)!!

    fun character(rawName: String, env: DataFetchingEnvironment): Character? {
        // JanusGraph will warn that this query requires iterating over all vertices.
        // This is because abstract Vertex classes are queried by union-ing queries of their base classes
        // Need to find a fix for this.
        val name = Name.parse(rawName)
        return env.graphMapper.allV<Character> {
            has("name.given", name.given).apply {
                if (name.surname != null) has("name.surname", name.surname)
            }
        }.toOptional().fetch()
    }
}

