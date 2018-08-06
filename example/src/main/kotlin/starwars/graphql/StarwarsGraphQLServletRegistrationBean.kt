package starwars.graphql

import com.coxautodev.graphql.tools.SchemaParser
import graphql.servlet.GraphQLInvocationInputFactory
import graphql.servlet.SimpleGraphQLHttpServlet
import graphql.servlet.ogm.GraphMapperGQLContextBuilder
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.stereotype.Component
import starwars.StarwarsGraphMapperSupplier
import starwars.graphql.character.CharacterQueryResolver
import starwars.graphql.droid.DroidMutationResolver
import starwars.graphql.droid.DroidQueryResolver
import starwars.graphql.droid.DroidTypeResolver
import starwars.graphql.human.HumanQueryResolver
import starwars.graphql.human.HumanTypeResolver

@Component
internal class StarwarsGraphQLServletRegistrationBean(
        characterQueryResolver: CharacterQueryResolver,
        humanTypeResolver: HumanTypeResolver,
        humanQueryResolver: HumanQueryResolver,
        droidTypeResolver: DroidTypeResolver,
        droidQueryResolver: DroidQueryResolver,
        droidMutationResolver: DroidMutationResolver,
        graphMapperSupplier: StarwarsGraphMapperSupplier,
        dataLoaderRegisterySupplier: DataLoaderRegisterySupplier
) : ServletRegistrationBean<SimpleGraphQLHttpServlet>(
        SimpleGraphQLHttpServlet.newBuilder(
                GraphQLInvocationInputFactory.newBuilder(
                        SchemaParser.newParser()
                                .file("starwars.graphqls")
                                .resolvers(
                                        characterQueryResolver,
                                        humanTypeResolver,
                                        humanQueryResolver,
                                        droidTypeResolver,
                                        droidQueryResolver,
                                        droidMutationResolver)
                                .build()
                                .makeExecutableSchema())
                        .withGraphQLContextBuilder(GraphMapperGQLContextBuilder(graphMapperSupplier, dataLoaderRegisterySupplier))
                        .build())
                .build(),
        "/graphql")

