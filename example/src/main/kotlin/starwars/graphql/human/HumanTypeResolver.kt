@file:Suppress("unused")

package starwars.graphql.human

import com.coxautodev.graphql.tools.GraphQLResolver
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.ogm.fetch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.stereotype.Component
import starwars.graphql.character.CharacterPage
import starwars.graphql.character.CharacterPageInfo
import starwars.graphql.character.CharacterPageOrder
import starwars.graphql.character.CharacterTypeResolver
import starwars.graphql.paginate
import starwars.models.Human
import java.util.concurrent.CompletableFuture

@ExperimentalCoroutinesApi
@Component
internal class HumanTypeResolver : CharacterTypeResolver, GraphQLResolver<Human> {

    fun getHomePlanet(human: Human): String? = human.homePlanet

    fun getTwinSiblings(
            human: Human,
            resume: HumanPageInfo?,
            limit: Int,
            order: HumanPageOrder,
            env: DataFetchingEnvironment
    ): CompletableFuture<HumanPage> = env.fetch {
        human.twins
                .load()
                .paginate(info = resume?.copy(limit = limit) ?: HumanPageInfo(order = order, limit = limit))
    }

    // These redundant overrides are necessary for graphql.tools

    fun getId(human: Human): Any = super.getId(character = human)

    fun getFriends(
            human: Human,
            resume: CharacterPageInfo?,
            limit: Int,
            order: CharacterPageOrder,
            env: DataFetchingEnvironment
    ): CompletableFuture<CharacterPage> = super.getFriends(
            character = human,
            resume = resume,
            limit = limit,
            order = order,
            env = env)

    fun getSecondDegreeFriends(
            human: Human,
            resume: CharacterPageInfo?,
            limit: Int,
            order: CharacterPageOrder,
            env: DataFetchingEnvironment
    ): CompletableFuture<CharacterPage> = super.getSecondDegreeFriends(
            character = human,
            resume = resume,
            limit = limit,
            order = order,
            env = env)
}
