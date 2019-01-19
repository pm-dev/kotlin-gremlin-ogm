package graphql.servlet.ogm

import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.structure.Transaction.CLOSE_BEHAVIOR.COMMIT
import org.apache.tinkerpop.gremlin.structure.util.TransactionException
import java.util.*
import javax.security.auth.Subject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.websocket.Session
import javax.websocket.server.HandshakeRequest

open class GraphMapperGQLContext(
        val graphMapper: GraphMapper,
        httpServletRequest: HttpServletRequest? = null,
        httpServletResponse: HttpServletResponse? = null,
        session: Session? = null,
        handshakeRequest: HandshakeRequest? = null,
        subject: Subject? = null
) : GraphQLContext(httpServletRequest, httpServletResponse, session, handshakeRequest, subject)

val DataFetchingEnvironment.graphMapper: GraphMapper
    get() = kotlin.run {
        val context = getContext<GraphQLContext>() as? GraphMapperGQLContext
                ?: throw IncorrectGraphQLContext(getContext())
        context.graphMapper
    }

private class IncorrectGraphQLContext(context: GraphQLContext) :
        RuntimeException("The DataFetchingEnvironment has context: $context which is not of type " +
                "${GraphMapperGQLContext::class}, meaning we cannot access a graph mapper.")

private class PermissionDenied(reason: String) :
        RuntimeException("Permission was denied attempting to run mutation. $reason")

sealed class PermissionResult {
    object Allow : PermissionResult()
    data class Deny(val reason: String) : PermissionResult()
}

fun <T> DataFetchingEnvironment.mutate(
        retry: Boolean = true,
        validatePermission: GraphMapper.(request: HttpServletRequest?) -> PermissionResult = { PermissionResult.Allow },
        mutation: GraphMapper.() -> T
): T {
    var triesRemaining = if (retry) 3 else 1
    while (triesRemaining-- > 0) {
        try {
            val request = getContext<GraphQLContext>().httpServletRequest.asNullable
            val permissionResult = graphMapper.validatePermission(request)
            return when (permissionResult) {
                is PermissionResult.Allow -> graphMapper.g.tx().onClose(COMMIT).use {
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

private val <T> Optional<T>.asNullable: T? get() = orElse(null)
