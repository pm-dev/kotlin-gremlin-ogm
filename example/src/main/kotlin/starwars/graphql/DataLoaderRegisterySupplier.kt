package starwars.graphql

import graphql.servlet.ogm.dataloaders.PathToManyDataLoader
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.stereotype.Component
import starwars.StarwarsGraphMapperSupplier
import starwars.models.Character
import starwars.models.Human
import starwars.traversals.character.secondDegreeFriends
import starwars.traversals.human.twinSiblings
import java.util.function.Supplier

enum class DataLoaderKey(val key: String) {
    FRIENDS("friends"),
    SECOND_DEGREE_FRIENDS("second_degree_friends"),
    TWINS("twins"),
}

@Component
internal class DataLoaderRegisterySupplier(
        private val graphMapperSupplier: StarwarsGraphMapperSupplier
) : Supplier<DataLoaderRegistry> {

    override fun get(): DataLoaderRegistry =
            DataLoaderRegistry().apply {
                val graphMapper = graphMapperSupplier.get()
                register(DataLoaderKey.FRIENDS.key, PathToManyDataLoader(Character.friends, graphMapper))
                register(DataLoaderKey.SECOND_DEGREE_FRIENDS.key, PathToManyDataLoader(Character.secondDegreeFriends, graphMapper))
                register(DataLoaderKey.TWINS.key, PathToManyDataLoader(Human.twinSiblings, graphMapper))
            }
}

fun <K, V> DataFetchingEnvironment.dataLoader(key: DataLoaderKey): DataLoader<K, V> =
        getContext<GraphQLContext>().dataLoaderRegistry.get().getDataLoader(key.key)
