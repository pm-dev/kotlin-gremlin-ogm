package graphql.servlet.ogm

import graphql.servlet.GraphQLContext
import graphql.servlet.GraphQLContextBuilder
import graphql.servlet.ogm.batchloaders.StepToManyBatchLoader
import graphql.servlet.ogm.batchloaders.StepToOptionalBatchLoader
import graphql.servlet.ogm.batchloaders.StepToSingleBatchLoader
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.steps.Step
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import java.util.*
import java.util.function.Supplier
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.websocket.Session
import javax.websocket.server.HandshakeRequest

open class GraphMapperGQLContextBuilder(
        private val graphMapperSupplier: Supplier<GraphMapper>,
        stepsWithDataLoaders: Set<Step<*, *>>
) : GraphQLContextBuilder {

    private val dataLoaderStepToKey: Map<Step<*, *>, String> =
            stepsWithDataLoaders.associate { it to UUID.randomUUID().toString() }

    override fun build(): GraphQLContext =
            GraphMapperGQLContext(graphMapperSupplier.get(), dataLoaderStepToKey).apply {
                setDataLoaderRegistry()
            }

    override fun build(session: Session, handshakeRequest: HandshakeRequest): GraphQLContext =
            GraphMapperGQLContext(graphMapperSupplier.get(), dataLoaderStepToKey, null, null, session, handshakeRequest, null).apply {
                setDataLoaderRegistry()
            }

    override fun build(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse): GraphQLContext =
            GraphMapperGQLContext(graphMapperSupplier.get(), dataLoaderStepToKey, httpServletRequest, httpServletResponse).apply {
                setDataLoaderRegistry()
            }

    private fun GraphMapperGQLContext.setDataLoaderRegistry() {
        val registry = DataLoaderRegistry()
        dataLoaderStepToKey.forEach { step, stringKey ->
            registry.register(stringKey, when (step) {
                is Step.ToMany -> DataLoader.newDataLoader(StepToManyBatchLoader(step, graphMapper))
                is Step.ToOptional -> DataLoader.newDataLoader(StepToOptionalBatchLoader(step, graphMapper))
                is Step.ToSingle -> DataLoader.newDataLoader(StepToSingleBatchLoader(step, graphMapper))
                else -> throw IllegalStateException("Unknown step cardinality")
            })
        }
        setDataLoaderRegistry(registry)
    }
}
