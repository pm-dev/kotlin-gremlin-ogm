package graphql.servlet.batched

import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.dataloader.DataLoaderRegistry
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

open class RequestScopedGraphQLContext(
        request: Optional<HttpServletRequest>,
        response: Optional<HttpServletResponse>,
        dataLoaderRegistry: DataLoaderRegistry,
        val graphMapper: GraphMapper
) : GraphQLContextWithDataLoader(request, response, dataLoaderRegistry)
