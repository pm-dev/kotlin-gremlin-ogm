package graphql.servlet.batched

import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

open class GraphQLContextWithDataLoader(
        request: Optional<HttpServletRequest>,
        response: Optional<HttpServletResponse>,
        private val dataLoaderRegistry: DataLoaderRegistry
) : GraphQLContext(request, response) {

    fun <K, V> dataLoader(key: String) = dataLoaderRegistry.getDataLoader<K, V>(key) ?: throw DataLoaderNotFound(key)
}

/**
 * Gets a data loader registered with a given key. The [DataFetchingEnvironment]'s context must be a
 * [GraphQLContextWithDataLoader] or else an exception is thrown. The context is guaranteed to be of type
 * [GraphQLContextWithDataLoader] if using [BatchedGraphQLServlet].
 *
 * @param K The type of key which the items cached by the requested data loader are stored.
 * @param V The type of value cached by the requested data loader.
 * @param key The key with which the data loader is registered in the DataLoaderRegistry built by
 * [BatchedGraphQLServlet]. An exception is thrown if this key is not
 * registered in the DataLoaderRegistry.
 */
fun <K, V> DataFetchingEnvironment.dataLoader(key: String): DataLoader<K, V> {
    val context = getContext<GraphQLContext>() as? GraphQLContextWithDataLoader
            ?: throw IncorrectGraphQLContext(key, getContext())
    return context.dataLoader<K, V>(key)
}

private class IncorrectGraphQLContext(key: String, context: GraphQLContext) :
        RuntimeException("The DataFetchingEnvironment has context: $context which is not of type " +
                "${GraphQLContextWithDataLoader::class}, meaning we cannot fetch a data loader for key $key")

private class DataLoaderNotFound(key: String) :
        RuntimeException("No DataLoader has been registered for key $key")
