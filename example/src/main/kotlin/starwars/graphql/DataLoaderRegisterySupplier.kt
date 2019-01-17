package starwars.graphql

import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import graphql.servlet.ogm.dataloaders.*
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.stereotype.Component
import starwars.StarwarsGraphMapperSupplier
import starwars.models.Character
import starwars.models.Human
import starwars.traversals.character.secondDegreeFriends
import starwars.traversals.human.twinSiblings
import java.util.function.Supplier

internal enum class DataLoaderKey(val path: Path<out Vertex, out Any>) {
    FRIENDS(path = Character.friends),
    SECOND_DEGREE_FRIENDS(path = Character.secondDegreeFriends),
    TWINS(path = Human.twinSiblings),
}

@Component
internal class DataLoaderRegisterySupplier(
        private val graphMapperSupplier: StarwarsGraphMapperSupplier
) : Supplier<DataLoaderRegistry> {

    override fun get(): DataLoaderRegistry =
            DataLoaderRegistry().apply {
                val graphMapper = graphMapperSupplier.get()
                DataLoaderKey.values().forEach { key ->
                    register(key.name, graphMapper.createDataLoader(key.path))
                }
            }
}

internal fun <K, V> DataFetchingEnvironment.dataLoader(key: DataLoaderKey): DataLoader<K, V> =
        getContext<GraphQLContext>().dataLoaderRegistry.get().getDataLoader(key.name)

private fun <FROM : Vertex, TO> GraphMapper.createDataLoader(path: Path<FROM, TO>): DataLoader<FROM, *> =
        when (path) {
            is Path.ToMany -> DataLoader.newDataLoader(BatchPathToManyLoader(path = path, graphMapper = this))
            is Path.ToOptional -> DataLoader.newDataLoader(BatchPathToOptionalLoader(path = path, graphMapper = this))
            is Path.ToSingle -> DataLoader.newDataLoader(BatchPathToSingleLoader(path = path, graphMapper = this))
            else -> throw IllegalStateException("Unknown path cardinality")
        }
