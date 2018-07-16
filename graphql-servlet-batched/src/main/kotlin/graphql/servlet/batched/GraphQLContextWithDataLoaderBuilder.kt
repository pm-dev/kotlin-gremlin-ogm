package graphql.servlet.batched

import graphql.servlet.GraphQLContext
import org.dataloader.DataLoaderRegistry
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

open class GraphQLContextWithDataLoaderBuilder {

    open operator fun invoke(
            request: Optional<HttpServletRequest>,
            response: Optional<HttpServletResponse>,
            dataLoaderRegistry: DataLoaderRegistry): GraphQLContext =
            GraphQLContextWithDataLoader(request, response, dataLoaderRegistry)
}
