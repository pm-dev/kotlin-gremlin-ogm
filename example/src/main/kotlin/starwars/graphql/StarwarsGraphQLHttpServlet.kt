package starwars.graphql

import com.coxautodev.graphql.tools.SchemaParser
import graphql.servlet.GraphQLConfiguration
import graphql.servlet.SimpleGraphQLHttpServlet
import graphql.servlet.ogm.GraphMapperGQLContextBuilder
import org.springframework.stereotype.Component
import starwars.StarwarsGraphMapperSupplier
import starwars.graphql.character.CharacterQueryResolver
import starwars.graphql.droid.DroidMutationResolver
import starwars.graphql.droid.DroidQueryResolver
import starwars.graphql.droid.DroidTypeResolver
import starwars.graphql.human.HumanQueryResolver
import starwars.graphql.human.HumanTypeResolver
import starwars.graphql.scalars.GraphQLTimestamp

@Component
internal class StarwarsGraphQLHttpServlet(
        characterQueryResolver: CharacterQueryResolver,
        humanTypeResolver: HumanTypeResolver,
        humanQueryResolver: HumanQueryResolver,
        droidTypeResolver: DroidTypeResolver,
        droidQueryResolver: DroidQueryResolver,
        droidMutationResolver: DroidMutationResolver,
        graphMapperSupplier: StarwarsGraphMapperSupplier,
        dataLoaderRegisterySupplier: DataLoaderRegisterySupplier
) : SimpleGraphQLHttpServlet() {

    private val config: GraphQLConfiguration = GraphQLConfiguration.with(
            SchemaParser.newParser()
                    .file("starwars.graphqls")
                    .resolvers(
                            characterQueryResolver,
                            humanTypeResolver,
                            humanQueryResolver,
                            droidTypeResolver,
                            droidQueryResolver,
                            droidMutationResolver)
                    .scalars(GraphQLTimestamp)
                    .build()
                    .makeExecutableSchema())
            .with(GraphMapperGQLContextBuilder(graphMapperSupplier, dataLoaderRegisterySupplier))
            .build()

    override fun getConfiguration(): GraphQLConfiguration = config
}
