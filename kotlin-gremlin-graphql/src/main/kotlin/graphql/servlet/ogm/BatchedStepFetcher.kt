@file:Suppress("unused")

package graphql.servlet.ogm

import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import kotlinx.coroutines.future.await
import org.apache.tinkerpop.gremlin.ogm.steps.Step
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.SingleBoundStep
import org.dataloader.DataLoader

data class BatchedStepFetcher(private val env: DataFetchingEnvironment) {

    val graphMapper get() = env.graphMapper

    suspend fun <FROM, TO> SingleBoundStep.ToOptional<FROM, TO>.load(): TO? =
            dataLoader<FROM, TO?>(step).load(from).await()

    suspend fun <FROM, TO> SingleBoundStep.ToSingle<FROM, TO>.load(): TO =
            dataLoader<FROM, TO>(step).load(from).await()

    suspend fun <FROM, TO> SingleBoundStep.ToMany<FROM, TO>.load(): List<TO> =
            dataLoader<FROM, List<TO>>(step).load(from).await()

    private fun <FROM, TO> dataLoader(step: Step<*, *>): DataLoader<FROM, TO> {
        val context = env.getContext<GraphQLContext>() as? GraphMapperGQLContext
                ?: throw IncorrectGraphQLContext(env.getContext())
        val dataLoaderRegistry = context.dataLoaderRegistry.get()
        val dataLoaderKey = context.dataLoaderKeyLookup[step] ?: throw DataLoaderKeyNotFound(step)
        return dataLoaderRegistry.getDataLoader(dataLoaderKey)
    }

    private class DataLoaderKeyNotFound(step: Step<*, *>) :
            RuntimeException("GraphMapperGQLContext.dataLoaderKey does not have a key for step $step")
}
