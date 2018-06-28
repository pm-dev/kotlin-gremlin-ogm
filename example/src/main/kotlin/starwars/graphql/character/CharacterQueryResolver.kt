package starwars.graphql.character

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component
import starwars.StarwarsGraphMapper
import starwars.models.Character
import starwars.models.Name
import kotlin.reflect.full.isSubclassOf

@Component
internal class CharacterQueryResolver(
        private val gm: StarwarsGraphMapper
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
        // TODO build a DSL for querying all elements with label and indexed property
        val labels = gm.graphDescription.vertexClasses.filter { vertexClass ->
            vertexClass.isSubclassOf(Character::class)
        }.map { vertexClass ->
            gm.graphDescription.getVertexDescription(vertexClass).label
        }
        return labels.mapNotNull { label ->
            gm.g.V().has(label, "name.first", name.first).apply {
                if (name.last != null) has("name.last", name.last)
            }.map { vertex ->
                gm.deserializeV<Character>(vertex.get())
            }.tryNext().orElse(null)
            // This should be union'd instead of executing a traversal for each label
            // unfortunately, union-ing these traversals causes a JanusGraph warning
            // that all vertices are being iterated over.
        }.firstOrNull()
    }
}

