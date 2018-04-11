package starwars.graphql.character

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.springframework.stereotype.Component
import starwars.models.Character
import starwars.models.Name

@Component
internal class CharacterQueryResolver(
        private val gm: GraphMapper
) : GraphQLQueryResolver {

    // TODO this would be more efficient if we filtered for the name before loading all characters
    fun hero(): Character = gm.loadAll<Character>().single { it.name == Name("Luke", "Skywalker") }

    // TODO this would be more efficient if we filtered for the name before loading all characters
    fun character(name: String): Character? = gm.loadAll<Character>().find { it.name.full == name }
}

