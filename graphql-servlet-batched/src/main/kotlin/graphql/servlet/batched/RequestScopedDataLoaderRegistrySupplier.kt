package graphql.servlet.batched

import graphql.servlet.GraphQLServletListener
import org.dataloader.DataLoaderRegistry
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * An interface for supporting [DataLoaderRegistry]s that are scoped to a single request.
 */
interface RequestScopedDataLoaderRegistrySupplier : DataLoaderRegistrySupplier {

    val threadLocal: ThreadLocal<DataLoaderRegistry>

    fun createRegistry(request: HttpServletRequest, response: HttpServletResponse) : DataLoaderRegistry

    override fun get(): DataLoaderRegistry = threadLocal.get()

    override fun onRequest(request: HttpServletRequest, response: HttpServletResponse): GraphQLServletListener.RequestCallback {
        val dataLoaderRegistry = createRegistry(request, response)
        threadLocal.set(dataLoaderRegistry)
        return object : GraphQLServletListener.RequestCallback {
            override fun onFinally(request: HttpServletRequest, response: HttpServletResponse) {
                threadLocal.remove()
            }
        }
    }
}
