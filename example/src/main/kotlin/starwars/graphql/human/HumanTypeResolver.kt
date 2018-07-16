package starwars.graphql.human

import com.coxautodev.graphql.tools.GraphQLResolver
import graphql.schema.DataFetchingEnvironment
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.springframework.stereotype.Component
import starwars.graphql.character.CharacterTypeResolver
import starwars.models.Human
import starwars.traversals.human.toTwinSiblings

@Component
internal class HumanTypeResolver(
        override val graph: GraphMapper
): CharacterTypeResolver, GraphQLResolver<Human> {

    fun getHomePlanet(human: Human): String? = human.homePlanet
    fun getTwinSiblings(human: Human): List<Human> = graph.traverse(human.toTwinSiblings()).fetch()

    // These redundant overrides are necessary for graphql.tools
    fun getId(node: Human) = super.getId(node)
    fun getName(character: Human) = super.getName(character)
    fun getAppearsIn(character: Human) = super.getAppearsIn(character)
    fun getFriends(character: Human, env: DataFetchingEnvironment) = super.getFriends(character, env)
    fun getSecondDegreeFriends(character: Human, limit: Int?) = super.getSecondDegreeFriends(character, limit)
}
