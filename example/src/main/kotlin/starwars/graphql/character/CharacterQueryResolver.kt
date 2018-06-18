package starwars.graphql.character

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.V
import org.springframework.stereotype.Component
import starwars.models.Character
import starwars.models.Name

@Component
internal class CharacterQueryResolver(
        private val gm: GraphMapper
) : GraphQLQueryResolver {

    fun hero(): Character = gm.V<Character>().filter { it.get().name == Name("Luke", "Skywalker") }.next()

    fun character(name: String): Character? = gm.V<Character>().filter { it.get().name.full == name }.tryNext().orElse(null)
}

