package starwars.graphql.droid

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.springframework.stereotype.Component
import starwars.models.Droid


@Component
internal class DroidQueryResolver(
        private val graph: GraphMapper
): GraphQLQueryResolver {

    fun droid(name: String): Droid? = graph.getV<Droid>().filter { it.get().name.full == name }.tryNext().orElse(null)
}
