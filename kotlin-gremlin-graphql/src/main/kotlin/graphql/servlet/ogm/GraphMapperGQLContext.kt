package graphql.servlet.ogm

import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import javax.security.auth.Subject
import javax.servlet.http.HttpServletRequest
import javax.websocket.server.HandshakeRequest

open class GraphMapperGQLContext(
        val graphMapper: GraphMapper,
        httpServletRequest: HttpServletRequest? = null,
        handshakeRequest: HandshakeRequest? = null,
        subject: Subject? = null
) : GraphQLContext(httpServletRequest, handshakeRequest, subject)

val DataFetchingEnvironment.graphMapper: GraphMapper
    get() = kotlin.run {
        val context = getContext<GraphQLContext>() as? GraphMapperGQLContext
                ?: throw IncorrectGraphQLContext(getContext())
        context.graphMapper
    }

private class IncorrectGraphQLContext(context: GraphQLContext) :
        RuntimeException("The DataFetchingEnvironment has context: $context which is not of type " +
                "${GraphMapperGQLContext::class}, meaning we cannot access a graph mapper.")
