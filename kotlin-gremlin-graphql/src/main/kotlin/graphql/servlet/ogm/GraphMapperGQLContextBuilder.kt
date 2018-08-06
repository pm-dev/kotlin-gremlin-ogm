package graphql.servlet.ogm

import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import graphql.servlet.GraphQLContextBuilder
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import java.util.function.Supplier
import javax.servlet.http.HttpServletRequest
import javax.websocket.server.HandshakeRequest

open class GraphMapperGQLContextBuilder(
        private val graphMapper: Supplier<GraphMapper>,
        private val dataLoaderRegistrySupplier: Supplier<DataLoaderRegistry>
) : GraphQLContextBuilder {

    override fun build(): GraphQLContext =
            GraphMapperGQLContext(graphMapper.get()).apply {
                setDataLoaderRegistry(dataLoaderRegistrySupplier.get())
            }

    override fun build(handshakeRequest: HandshakeRequest?): GraphQLContext =
            GraphMapperGQLContext(graphMapper.get(), null, handshakeRequest, null).apply {
                setDataLoaderRegistry(dataLoaderRegistrySupplier.get())
            }

    override fun build(httpServletRequest: HttpServletRequest?): GraphQLContext =
            GraphMapperGQLContext(graphMapper.get(), httpServletRequest).apply {
                setDataLoaderRegistry(dataLoaderRegistrySupplier.get())
            }
}

fun <K, V> DataFetchingEnvironment.dataLoader(key: String): DataLoader<K, V> =
        getContext<GraphQLContext>().dataLoaderRegistry.get().getDataLoader(key)
