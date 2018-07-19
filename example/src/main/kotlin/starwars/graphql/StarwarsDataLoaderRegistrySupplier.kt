package starwars.graphql

import framework.PathToManyDataLoader
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.batched.RequestScopedDataLoaderRegistrySupplier
import graphql.servlet.batched.dataLoader
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.stereotype.Component
import starwars.models.Character
import starwars.models.Human
import starwars.traversals.character.secondDegreeFriends
import starwars.traversals.human.twinSiblings
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
internal class StarwarsDataLoaderRegistrySupplier(
        private val graphMapper: GraphMapper
) : RequestScopedDataLoaderRegistrySupplier {

    override val threadLocal: ThreadLocal<DataLoaderRegistry> = ThreadLocal()

    override fun createRegistry(request: HttpServletRequest, response: HttpServletResponse) =
            DataLoaderRegistry().apply {
                register(StarwarsPath.FRIENDS, PathToManyDataLoader(Character.friends, graphMapper))
                register(StarwarsPath.SECOND_DEGREE_FRIENDS, PathToManyDataLoader(Character.secondDegreeFriends, graphMapper))
                register(StarwarsPath.TWINS, PathToManyDataLoader(Human.twinSiblings, graphMapper))
            }

    private fun DataLoaderRegistry.register(key: StarwarsPath, loader: DataLoader<*, *>) = register(key.dataLoaderRegistryKey, loader)
}

internal fun <K, V> DataFetchingEnvironment.dataLoader(loader: StarwarsPath): DataLoader<K, V>
        = dataLoader(loader.dataLoaderRegistryKey)

