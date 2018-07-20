package starwars.graphql

import com.coxautodev.graphql.tools.SchemaParser
import graphql.servlet.batched.dataloaders.PathToManyDataLoader
import graphql.servlet.DefaultGraphQLSchemaProvider
import graphql.servlet.batched.BatchedGraphQLServlet
import graphql.servlet.batched.GraphMapperFactory
import graphql.servlet.batched.RequestScopedDataLoaderRegistry
import graphql.servlet.batched.RequestScopedGraphQLContextBuilder
import org.dataloader.DataLoaderRegistry
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.stereotype.Component
import starwars.graphql.character.CharacterQueryResolver
import starwars.graphql.droid.DroidMutationResolver
import starwars.graphql.droid.DroidQueryResolver
import starwars.graphql.droid.DroidTypeResolver
import starwars.graphql.human.HumanQueryResolver
import starwars.graphql.human.HumanTypeResolver
import starwars.models.Character
import starwars.traversals.character.secondDegreeFriends
import starwars.traversals.human.twinSiblings
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
internal class StarwarsGraphQLServletRegistrationBean(
        characterQueryResolver: CharacterQueryResolver,
        humanTypeResolver: HumanTypeResolver,
        humanQueryResolver: HumanQueryResolver,
        droidTypeResolver: DroidTypeResolver,
        droidQueryResolver: DroidQueryResolver,
        droidMutationResolver: DroidMutationResolver,
        graphMapperFactory: GraphMapperFactory
) : ServletRegistrationBean<BatchedGraphQLServlet>(
        BatchedGraphQLServlet(

                // 1) Register all resolvers here
                schemaProvider = DefaultGraphQLSchemaProvider(SchemaParser.newParser()
                        .file("starwars.graphqls")
                        .resolvers(
                                characterQueryResolver,
                                humanTypeResolver,
                                humanQueryResolver,
                                droidTypeResolver,
                                droidQueryResolver,
                                droidMutationResolver)
                        .build().makeExecutableSchema()),

                registrySupplier = object : RequestScopedDataLoaderRegistry {

                    override val threadLocal: ThreadLocal<DataLoaderRegistry> = ThreadLocal()

                    // 2) Register all paths to be fetched using a batched data-loader
                    override fun createRegistry(request: HttpServletRequest, response: HttpServletResponse) = DataLoaderRegistry().apply {
                        val graphMapper = graphMapperFactory()
                        register(DataLoaderKey.FRIENDS.key, PathToManyDataLoader(Character.friends, graphMapper))
                        register(DataLoaderKey.SECOND_DEGREE_FRIENDS.key, PathToManyDataLoader(starwars.models.Character.secondDegreeFriends, graphMapper))
                        register(DataLoaderKey.TWINS.key, PathToManyDataLoader(starwars.models.Human.twinSiblings, graphMapper))
                    }
                },

                contextBuilder = object : RequestScopedGraphQLContextBuilder {

                    override val graphMapperFactory: GraphMapperFactory get() = graphMapperFactory
                }),
        "/graphql")

