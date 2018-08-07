package starwars.graphql.droid

import com.coxautodev.graphql.tools.GraphQLResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import starwars.graphql.character.CharacterTypeResolver
import starwars.models.Droid

@Component
internal class DroidTypeResolver : CharacterTypeResolver, GraphQLResolver<Droid> {

    // These redundant overrides are necessary for graphql.tools

    fun getId(node: Droid) = super.getId(node)

    fun getAppearsIn(character: Droid) = super.getAppearsIn(character)

    fun getFriends(character: Droid, env: DataFetchingEnvironment) = super.getFriends(character, env)

    fun getSecondDegreeFriends(character: Droid, limit: Int?, env: DataFetchingEnvironment) = super.getSecondDegreeFriends(character, limit, env)
}
