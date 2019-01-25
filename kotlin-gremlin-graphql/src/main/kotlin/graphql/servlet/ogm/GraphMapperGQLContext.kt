package graphql.servlet.ogm

import graphql.servlet.GraphQLContext
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.steps.Step
import javax.security.auth.Subject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.websocket.Session
import javax.websocket.server.HandshakeRequest

open class GraphMapperGQLContext(
        internal val graphMapper: GraphMapper,
        internal val dataLoaderKeyLookup: Map<Step<*, *>, String>,
        httpServletRequest: HttpServletRequest? = null,
        httpServletResponse: HttpServletResponse? = null,
        session: Session? = null,
        handshakeRequest: HandshakeRequest? = null,
        subject: Subject? = null
) : GraphQLContext(httpServletRequest, httpServletResponse, session, handshakeRequest, subject)
