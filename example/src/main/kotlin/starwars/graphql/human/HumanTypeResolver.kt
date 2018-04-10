package starwars.graphql.human

import com.coxautodev.graphql.tools.GraphQLResolver
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.springframework.stereotype.Component
import starwars.graphql.character.CharacterTypeResolver
import starwars.models.Human

@Component
class HumanTypeResolver(
        override val graph: GraphMapper
): CharacterTypeResolver, GraphQLResolver<Human> {

    fun getHomePlanet(human: Human): String? = human.homePlanet

    // These redundant overrides are necessary for graphql.tools
    fun getId(node: Human) = super.getId(node)
    fun getName(character: Human) = super.getName(character)
    fun getAppearsIn(character: Human) = super.getAppearsIn(character)
    fun getFriends(character: Human) = super.getFriends(character)
    fun getSecondDegreeFriends(character: Human, limit: Int?) = super.getSecondDegreeFriends(character, limit)
}
