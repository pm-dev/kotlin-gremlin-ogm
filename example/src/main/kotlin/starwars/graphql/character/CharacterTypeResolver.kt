package starwars.graphql.character

import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.paths.bound.from
import starwars.models.Character
import starwars.models.Character.Companion.friends
import starwars.models.Episode
import starwars.models.Name
import starwars.traversals.character.toSecondDegreeFriends
import java.util.*


internal interface CharacterTypeResolver {

    val graph: GraphMapper

    fun getId(character: Character): Any = character.id ?: UUID.randomUUID().toString()
    fun getName(character: Character): Name = character.name
    fun getAppearsIn(character: Character): Set<Episode> = character.appearsIn
    fun getFriends(character: Character): List<Character> = graph.traverse(friends from character).fetch()
    fun getSecondDegreeFriends(character: Character, limit: Int?): List<Character> {
        val range = if (limit == null) null else 0 until limit.toLong()
        return graph.traverse(character.toSecondDegreeFriends(range)).fetch()
    }
}
