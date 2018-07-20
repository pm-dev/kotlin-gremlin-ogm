package starwars.graphql.character

import graphql.schema.DataFetchingEnvironment
import starwars.graphql.DataLoaderKey
import starwars.graphql.dataLoader
import starwars.models.Character
import starwars.models.Episode
import starwars.models.Name
import java.util.*
import java.util.concurrent.CompletableFuture

internal interface CharacterTypeResolver {

    fun getId(character: Character): Any = character.id ?: UUID.randomUUID().toString()

    fun getName(character: Character): CompletableFuture<Name> = CompletableFuture.completedFuture(character.name)

    fun getAppearsIn(character: Character): Set<Episode> = character.appearsIn

    fun getFriends(character: Character, env: DataFetchingEnvironment): CompletableFuture<List<Character>> =
            env.dataLoader<Character, List<Character>>(DataLoaderKey.FRIENDS).load(character)

    fun getSecondDegreeFriends(character: Character, limit: Int?, env: DataFetchingEnvironment): CompletableFuture<List<Character>> =
            env.dataLoader<Character, List<Character>>(DataLoaderKey.SECOND_DEGREE_FRIENDS).load(character).apply {
                if (limit != null) {
                    thenApply { characters ->
                        characters.slice(0 until limit)
                    }
                }
            }
}
