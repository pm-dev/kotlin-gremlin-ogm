package graphql.servlet.batched

import graphql.servlet.GraphQLContext
import org.dataloader.DataLoaderRegistry
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

interface RequestScopedGraphQLContextBuilder : GraphQLContextWithDataLoaderBuilder {

    val graphMapperFactory: GraphMapperFactory

    override fun build(request: Optional<HttpServletRequest>,
                       response: Optional<HttpServletResponse>,
                       dataLoaderRegistry: DataLoaderRegistry
    ): GraphQLContext = RequestScopedGraphQLContext(request, response, dataLoaderRegistry, graphMapperFactory())
}
