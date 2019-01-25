package starwars.graphql

import com.coxautodev.graphql.tools.SchemaParser
import graphql.schema.pagination.GraphQLPageCursor
import graphql.servlet.GraphQLConfiguration
import graphql.servlet.SimpleGraphQLHttpServlet
import graphql.servlet.ogm.GraphMapperGQLContextBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.stereotype.Component
import starwars.StarwarsGraphMapperSupplier
import starwars.graphql.character.CharacterMutationResolver
import starwars.graphql.character.CharacterPageInfo
import starwars.graphql.character.CharacterQueryResolver
import starwars.graphql.droid.DroidMutationResolver
import starwars.graphql.droid.DroidQueryResolver
import starwars.graphql.droid.DroidTypeResolver
import starwars.graphql.human.HumanPageInfo
import starwars.graphql.human.HumanQueryResolver
import starwars.graphql.human.HumanTypeResolver
import starwars.graphql.scalars.GraphQLTimestamp
import starwars.graphql.scalars.GraphQLPageLimit
import starwars.models.Character
import starwars.models.Human
import starwars.traversals.character.secondDegreeFriends
import starwars.traversals.human.twinSiblings

@ExperimentalCoroutinesApi
@Component
internal class StarwarsGraphQLHttpServlet(
        characterQueryResolver: CharacterQueryResolver,
        characterMutationResolver: CharacterMutationResolver,
        humanTypeResolver: HumanTypeResolver,
        humanQueryResolver: HumanQueryResolver,
        droidTypeResolver: DroidTypeResolver,
        droidQueryResolver: DroidQueryResolver,
        droidMutationResolver: DroidMutationResolver,
        graphMapperSupplier: StarwarsGraphMapperSupplier
) : SimpleGraphQLHttpServlet() {

    private val config: GraphQLConfiguration = GraphQLConfiguration.with(
            SchemaParser.newParser()
                    .files(
                            "graphqls/Character.graphqls",
                            "graphqls/Droid.graphqls",
                            "graphqls/Episode.graphqls",
                            "graphqls/Human.graphqls",
                            "graphqls/Mutation.graphqls",
                            "graphqls/Name.graphqls",
                            "graphqls/Node.graphqls",
                            "graphqls/Query.graphqls",
                            "graphqls/Scalars.graphqls")
                    .resolvers(
                            characterQueryResolver,
                            characterMutationResolver,
                            humanTypeResolver,
                            humanQueryResolver,
                            droidTypeResolver,
                            droidQueryResolver,
                            droidMutationResolver)
                    .scalars(
                            GraphQLTimestamp,
                            GraphQLPageLimit,
                            GraphQLPageCursor("CharacterPageCursor", CharacterPageInfo::class.java),
                            GraphQLPageCursor("HumanPageCursor", HumanPageInfo::class.java)
                    )
                    .build()
                    .makeExecutableSchema())
            .with(GraphMapperGQLContextBuilder(graphMapperSupplier, setOf(
                    Character.friends,
                    Character.secondDegreeFriends,
                    Human.twinSiblings)))
            .build()

    override fun getConfiguration(): GraphQLConfiguration = config
}
