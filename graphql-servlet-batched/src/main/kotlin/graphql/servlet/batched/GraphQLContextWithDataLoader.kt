package graphql.servlet.batched

import graphql.servlet.GraphQLContext
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

    private class DataLoaderNotFound(key: String) : RuntimeException("No DataLoader has been registered for key $key")
}
