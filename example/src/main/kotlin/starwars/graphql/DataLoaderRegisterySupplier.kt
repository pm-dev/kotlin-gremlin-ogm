package starwars.graphql

import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import graphql.servlet.ogm.dataloaders.PathToManyBatchLoader
import graphql.servlet.ogm.dataloaders.PathToOptionalBatchLoader
import graphql.servlet.ogm.dataloaders.PathToSingleBatchLoader
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.stereotype.Component
import starwars.StarwarsGraphMapperSupplier
import starwars.models.Character
import starwars.models.Human
import starwars.traversals.character.secondDegreeFriends
import starwars.traversals.human.twinSiblings
import java.util.function.Supplier

internal enum class DataLoaderKey(val step: Step<out Vertex, out Any>) {
    FRIENDS(step = Character.friends),
    SECOND_DEGREE_FRIENDS(step = Character.secondDegreeFriends),
    TWINS(step = Human.twinSiblings),
}

@Component
internal class DataLoaderRegisterySupplier(
        private val graphMapperSupplier: StarwarsGraphMapperSupplier
) : Supplier<DataLoaderRegistry> {

    override fun get(): DataLoaderRegistry =
            DataLoaderRegistry().apply {
                val graphMapper = graphMapperSupplier.get()
                DataLoaderKey.values().forEach { key ->
                    register(key.name, graphMapper.createDataLoader(key.step))
                }
            }
}

internal fun <K, V> DataFetchingEnvironment.dataLoader(key: DataLoaderKey): DataLoader<K, V> =
        getContext<GraphQLContext>().dataLoaderRegistry.get().getDataLoader(key.name)

private fun <FROM : Vertex, TO> GraphMapper.createDataLoader(step: Step<FROM, TO>): DataLoader<FROM, *> =
        when (step) {
            is Step.ToMany -> DataLoader.newDataLoader(PathToManyBatchLoader(step = step, graphMapper = this))
            is Step.ToOptional -> DataLoader.newDataLoader(PathToOptionalBatchLoader(step = step, graphMapper = this))
            is Step.ToSingle -> DataLoader.newDataLoader(PathToSingleBatchLoader(step = step, graphMapper = this))
            else -> throw IllegalStateException("Unknown step cardinality")
        }
