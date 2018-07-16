package starwars.graphql

import graphql.servlet.GraphQLSchemaProvider
import graphql.servlet.batched.BatchedGraphQLServlet
import graphql.servlet.batched.DataLoaderRegistrySupplier
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.stereotype.Component

@Component
internal class StarwarsGraphQLServletRegistrationBean(
        schemaProvider: GraphQLSchemaProvider,
        registryBuilder: DataLoaderRegistrySupplier
): ServletRegistrationBean<BatchedGraphQLServlet>(BatchedGraphQLServlet(schemaProvider, registryBuilder), "/graphql")

