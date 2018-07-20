package graphql.servlet.batched

import graphql.servlet.GraphQLServletListener
import org.dataloader.DataLoaderRegistry
import java.util.function.Supplier
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * An object that supplies a [DataLoaderRegistry] to the [BatchedGraphQLServlet]. This object will
 * also receive [GraphQLServletListener] calls notifying the object of when requests and operations are created and
 * finish, which can be useful if the DataLoaderRegistry should be scoped to a request.
 */
interface DataLoaderRegistrySupplier : Supplier<DataLoaderRegistry>, GraphQLServletListener

/**
 * An interface for supporting [DataLoaderRegistry]s that are scoped to a single request.
 */
interface RequestScopedDataLoaderRegistry : DataLoaderRegistrySupplier {

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
