package graphql.servlet.batched

import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import org.apache.tinkerpop.gremlin.ogm.GraphMapper

val DataFetchingEnvironment.graphMapper: GraphMapper
    get() = kotlin.run {
        val context = getContext<GraphQLContext>() as? RequestScopedGraphQLContext
                ?: throw IncorrectGraphQLContext(getContext())
        context.graphMapper
    }

private class IncorrectGraphQLContext(context: GraphQLContext) :
        RuntimeException("The DataFetchingEnvironment has context: $context which is not of type " +
                "${RequestScopedGraphQLContext::class}, meaning we cannot access a graph mapper.")
