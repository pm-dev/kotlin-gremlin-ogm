package starwars.graphql.character

import graphql.schema.DataFetchingEnvironment
import starwars.graphql.DataLoaderKey
import starwars.graphql.dataLoader
import starwars.models.Character
import starwars.models.Episode
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.math.min

internal interface CharacterTypeResolver {

    fun getId(character: Character): Any = character.id ?: UUID.randomUUID().toString()

    fun getAppearsIn(character: Character): Set<Episode> = character.appearsIn

    fun getFriends(character: Character, env: DataFetchingEnvironment): CompletableFuture<List<Character>> =
            env.dataLoader<Character, List<Character>>(DataLoaderKey.FRIENDS).load(character)

    fun getSecondDegreeFriends(character: Character, limit: Int?, env: DataFetchingEnvironment): CompletableFuture<List<Character>> {
        return env.dataLoader<Character, List<Character>>(DataLoaderKey.SECOND_DEGREE_FRIENDS).load(character)
                .thenApply { characters ->
                    if (limit == null) characters
                    else characters.slice(0 until min(characters.size, limit))
                }
    }
}
