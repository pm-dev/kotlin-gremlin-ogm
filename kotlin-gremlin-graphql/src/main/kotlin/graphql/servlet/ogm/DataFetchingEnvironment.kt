package graphql.servlet.ogm

import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.structure.Transaction
import org.apache.tinkerpop.gremlin.structure.util.TransactionException
import java.util.concurrent.CompletableFuture
import javax.servlet.http.HttpServletRequest

@ExperimentalCoroutinesApi
fun <TO> DataFetchingEnvironment.fetch(block: suspend BatchedStepFetcher.() -> TO): CompletableFuture<TO> =
        GlobalScope.future(start = CoroutineStart.UNDISPATCHED) {
            BatchedStepFetcher(this@fetch).block()
        }

fun <T> DataFetchingEnvironment.mutate(
        retry: Boolean = true,
        validatePermission: GraphMapper.(request: HttpServletRequest?) -> PermissionResult = { PermissionResult.Allow },
        mutation: GraphMapper.() -> T
): T {
    var triesRemaining = if (retry) 3 else 1
    while (triesRemaining-- > 0) {
        try {
            val request = getContext<GraphQLContext>().httpServletRequest.orElse(null)?.let { it }
            val permissionResult = graphMapper.validatePermission(request)
            return when (permissionResult) {
                is PermissionResult.Allow -> graphMapper.g.tx().onClose(Transaction.CLOSE_BEHAVIOR.COMMIT).use {
                    graphMapper.mutation()
                }
                is PermissionResult.Deny -> throw PermissionDenied(permissionResult.reason)
            }
        } catch (e: TransactionException) {
            if (triesRemaining == 0) {
                graphMapper.g.tx().rollback()
                throw e
            }
        } catch (e: Exception) {
            graphMapper.g.tx().rollback()
            throw e
        }
    }
    throw TransactionException("Unknown transaction exception")
}

internal val DataFetchingEnvironment.graphMapper: GraphMapper
    get() {
        val context = getContext<GraphQLContext>() as? GraphMapperGQLContext
                ?: throw IncorrectGraphQLContext(getContext())
        return context.graphMapper
    }

internal class IncorrectGraphQLContext(context: GraphQLContext) :
        RuntimeException("The DataFetchingEnvironment has context: $context which is not of type " +
                "${GraphMapperGQLContext::class}, meaning we cannot access a graph mapper.")

private class PermissionDenied(reason: String) :
        RuntimeException("Permission was denied attempting to run mutation. $reason")
