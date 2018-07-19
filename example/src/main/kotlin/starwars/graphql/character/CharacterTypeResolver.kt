package starwars.graphql.character

import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory
import starwars.graphql.StarwarsPath
import starwars.graphql.dataLoader
import starwars.models.Character
import starwars.models.Episode
import starwars.models.Name
import java.util.*
import java.util.concurrent.CompletableFuture


internal interface CharacterTypeResolver {

    companion object {
        private val logger = LoggerFactory.getLogger(CharacterTypeResolver::class.java)
    }

    fun getId(character: Character): Any = character.id ?: UUID.randomUUID().toString()

    fun getName(character: Character): CompletableFuture<Name> = CompletableFuture.completedFuture(character.name)

    fun getAppearsIn(character: Character): Set<Episode> = character.appearsIn

    fun getFriends(character: Character, env: DataFetchingEnvironment): CompletableFuture<List<Character>> {
        logger.debug("Getting friends for ${character.name}")
        return env.dataLoader<Character, List<Character>>(StarwarsPath.FRIENDS).load(character)
    }

    fun getSecondDegreeFriends(character: Character, limit: Int?, env: DataFetchingEnvironment): CompletableFuture<List<Character>> {
        return env.dataLoader<Character, List<Character>>(StarwarsPath.SECOND_DEGREE_FRIENDS).load(character).apply {
            if (limit != null) {
                thenApply { characters ->
                    characters.slice(0 until limit)
                }
            }
        }
    }
}
