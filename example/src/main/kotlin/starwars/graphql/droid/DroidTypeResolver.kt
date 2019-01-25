@file:Suppress("unused")

package starwars.graphql.droid

import com.coxautodev.graphql.tools.GraphQLResolver
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.stereotype.Component
import starwars.graphql.character.CharacterPage
import starwars.graphql.character.CharacterPageInfo
import starwars.graphql.character.CharacterPageOrder
import starwars.graphql.character.CharacterTypeResolver
import starwars.models.Droid
import java.util.concurrent.CompletableFuture

@ExperimentalCoroutinesApi
@Component
internal class DroidTypeResolver : CharacterTypeResolver, GraphQLResolver<Droid> {

    // These redundant overrides are necessary for graphql.tools

    fun getId(droid: Droid): Any = super.getId(character = droid)

    fun getFriends(
            droid: Droid,
            resume: CharacterPageInfo?,
            limit: Int,
            order: CharacterPageOrder,
            env: DataFetchingEnvironment
    ): CompletableFuture<CharacterPage> = super.getFriends(
            character = droid,
            resume = resume,
            limit = limit,
            order = order,
            env = env)

    fun getSecondDegreeFriends(
            droid: Droid,
            resume: CharacterPageInfo?,
            limit: Int,
            order: CharacterPageOrder,
            env: DataFetchingEnvironment
    ): CompletableFuture<CharacterPage> = super.getSecondDegreeFriends(
            character = droid,
            resume = resume,
            limit = limit,
            order = order,
            env = env)
}
