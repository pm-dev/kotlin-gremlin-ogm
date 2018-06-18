package starwars.graphql.human

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.V
import org.springframework.stereotype.Component
import starwars.models.Human

@Component
internal class HumanQueryResolver(
        private val graph: GraphMapper
) : GraphQLQueryResolver {

    fun human(name: String): Human? = graph.V<Human>().filter { it.get().name.full == name }.tryNext().orElse(null)
}
