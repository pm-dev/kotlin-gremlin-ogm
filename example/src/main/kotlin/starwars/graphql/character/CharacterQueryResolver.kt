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

    fun hero(): Character = character("Luke Skywalker")!!

    /**
     * We could have executed this query with the following line:
     *
     * gm.V<Character>().filter { it.get().name == Name.parse(rawName) }.tryNext().orElse(null)
     *
     * however, this would have required iterating over all Characters. In places where all elements are queried,
     * it's more efficient to use Gremlin directly on properties that have an index, then call GraphMapper.deserialize
     */
    fun character(rawName: String): Character? {
        val name = Name.parse(rawName)
        return gm.g.V().has("name.first", name.first).apply {
            if (name.last != null) has("name.last", name.last)
        }.map { gm.deserializeV<Character>(it.get()) }.tryNext().orElse(null)
    }
}

