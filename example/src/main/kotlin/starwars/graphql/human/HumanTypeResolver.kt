package starwars.graphql.human

import com.coxautodev.graphql.tools.GraphQLResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import starwars.graphql.DataLoaderKey
import starwars.graphql.character.CharacterTypeResolver
import starwars.graphql.dataLoader
import starwars.models.Human
import java.util.concurrent.CompletableFuture

@Component
internal class HumanTypeResolver : CharacterTypeResolver, GraphQLResolver<Human> {

    fun getHomePlanet(human: Human): String? = human.homePlanet

    fun getTwinSiblings(human: Human, env: DataFetchingEnvironment): CompletableFuture<List<Human>> =
            env.dataLoader<Human, List<Human>>(DataLoaderKey.TWINS).load(human)

    // These redundant overrides are necessary for graphql.tools
    fun getId(node: Human) = super.getId(node)

    fun getAppearsIn(character: Human) = super.getAppearsIn(character)

    fun getFriends(character: Human, env: DataFetchingEnvironment) = super.getFriends(character, env)

    fun getSecondDegreeFriends(character: Human, limit: Int?, env: DataFetchingEnvironment) = super.getSecondDegreeFriends(character, limit, env)
}
