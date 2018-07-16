package starwars.graphql.character

import graphql.schema.DataFetchingEnvironment
import graphql.servlet.batched.dataLoader
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.paths.bound.from
import org.slf4j.LoggerFactory
import starwars.models.Character
import starwars.models.Character.Companion.friends
import starwars.models.Episode
import starwars.models.Name
import starwars.traversals.character.toSecondDegreeFriends
import java.util.*
import java.util.concurrent.CompletableFuture


internal interface CharacterTypeResolver {

    companion object {
        private val logger = LoggerFactory.getLogger(CharacterTypeResolver::class.java)
    }

    val graph: GraphMapper

    fun getId(character: Character): Any = character.id ?: UUID.randomUUID().toString()
    fun getName(character: Character): CompletableFuture<Name> = CompletableFuture.completedFuture(character.name)
    fun getAppearsIn(character: Character): Set<Episode> = character.appearsIn
    fun getFriends(character: Character, env: DataFetchingEnvironment): CompletableFuture<List<Character>> {
        val ids = graph.traverse(friends from character).fetch().map { it.id!! }
        logger.debug("Getting friends for ${character.name} with ids $ids")
        return env.dataLoader<Long, Character>(CharacterDataLoader.registryKey).loadMany(ids)
    }
    fun getSecondDegreeFriends(character: Character, limit: Int?): List<Character> {
        val range = if (limit == null) null else 0 until limit.toLong()
        return graph.traverse(character.toSecondDegreeFriends(range)).fetch()
    }
}
