package graphql.servlet.batched

import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentationOptions
import graphql.execution.preparsed.NoOpPreparsedDocumentProvider
import graphql.execution.preparsed.PreparsedDocumentProvider
import graphql.servlet.*
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * A [GraphQLServlet] that enables batched data-loading. When using this servlet,
 * type resolvers can call DataFetchingEnvironment.dataLoader(key).load() to batch-fetch and cache values.
 */
class BatchedGraphQLServlet(
        private val schemaProvider: GraphQLSchemaProvider,
        private val registrySupplier: DataLoaderRegistrySupplier,
        private val rootObjectBuilder: GraphQLRootObjectBuilder = DefaultGraphQLRootObjectBuilder(),
        private val contextBuilder: GraphQLContextWithDataLoaderBuilder = GraphQLContextWithDataLoaderBuilder(),
        private val errorHandler: GraphQLErrorHandler = DefaultGraphQLErrorHandler(),
        private val executionStrategyProvider: ExecutionStrategyProvider = DefaultExecutionStrategyProvider(),
        private val preparsedDocumentProvider: PreparsedDocumentProvider = NoOpPreparsedDocumentProvider.INSTANCE,
        private val dataLoaderDispatcherInstrumentationOptions: DataLoaderDispatcherInstrumentationOptions = DataLoaderDispatcherInstrumentationOptions.newOptions(),
        objectMapperConfigurer: ObjectMapperConfigurer? = null,
        listeners: List<GraphQLServletListener> = emptyList()
) : GraphQLServlet(objectMapperConfigurer, listeners.plus(registrySupplier)) {

    override fun createContext(request: Optional<HttpServletRequest>, response: Optional<HttpServletResponse>): GraphQLContext = contextBuilder(request, response, registrySupplier.get())

    override fun createRootObject(request: Optional<HttpServletRequest>, response: Optional<HttpServletResponse>): Any = rootObjectBuilder.build(request, response)

    override fun getExecutionStrategyProvider(): ExecutionStrategyProvider = executionStrategyProvider

    override fun getPreparsedDocumentProvider(): PreparsedDocumentProvider = preparsedDocumentProvider

    override fun getInstrumentation(): Instrumentation = DataLoaderDispatcherInstrumentation(registrySupplier.get(), dataLoaderDispatcherInstrumentationOptions)

    override fun getSchemaProvider(): GraphQLSchemaProvider = schemaProvider

    override fun getGraphQLErrorHandler(): GraphQLErrorHandler = errorHandler
}
