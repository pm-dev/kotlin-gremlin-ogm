package graphql.servlet.batched

import graphql.servlet.GraphQLServletListener
import org.dataloader.DataLoaderRegistry
import java.util.function.Supplier

/**
 * An object that supplies a [DataLoaderRegistry] to the [BatchedGraphQLServlet]. This object will
 * also receive [GraphQLServletListener] calls notifying the object of when requests and operations are created and
 * finish, which can be useful if the DataLoaderRegistry should be scoped to a request.
 */
interface DataLoaderRegistrySupplier : Supplier<DataLoaderRegistry>, GraphQLServletListener
