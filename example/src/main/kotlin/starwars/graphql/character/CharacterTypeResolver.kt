package starwars.graphql.character

import graphql.schema.DataFetchingEnvironment
import graphql.servlet.ogm.fetch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import starwars.graphql.paginate
import starwars.models.Character
import java.util.*
import java.util.concurrent.CompletableFuture

@ExperimentalCoroutinesApi
internal interface CharacterTypeResolver {

    fun getId(character: Character): Any = character.id ?: UUID.randomUUID().toString()

    fun getFriends(
            character: Character,
            resume: CharacterPageInfo?,
            limit: Int,
            order: CharacterPageOrder,
            env: DataFetchingEnvironment
    ): CompletableFuture<CharacterPage> = env.fetch {
        character.friends
                .load()
                .paginate(info = resume?.copy(limit = limit) ?: CharacterPageInfo(order = order, limit = limit))
    }

    fun getSecondDegreeFriends(
            character: Character,
            resume: CharacterPageInfo?,
            limit: Int,
            order: CharacterPageOrder,
            env: DataFetchingEnvironment
    ): CompletableFuture<CharacterPage> = env.fetch {
        character.secondDegreeFriends
                .load()
                .paginate(info = resume?.copy(limit = limit) ?: CharacterPageInfo(order = order, limit = limit))
    }
}


