package starwars.graphql

import graphql.servlet.batched.RequestScopedDataLoaderRegistrySupplier
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.dataloader.DataLoaderRegistry
import org.springframework.stereotype.Component
import starwars.graphql.character.CharacterDataLoader
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
internal class StarwarsDataLoaderRegistrySupplier(
        private val graphMapper: GraphMapper
) : RequestScopedDataLoaderRegistrySupplier {

    override val threadLocal: ThreadLocal<DataLoaderRegistry> = ThreadLocal()

    override fun createRegistry(request: HttpServletRequest, response: HttpServletResponse) =
            DataLoaderRegistry().apply {
                register(CharacterDataLoader.registryKey, CharacterDataLoader(graphMapper))
            }
}
