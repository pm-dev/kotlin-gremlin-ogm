package starwars.graphql.human

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.allV
import org.springframework.stereotype.Component
import starwars.models.Human
import starwars.models.Name

@Component
internal class HumanQueryResolver(
        private val graph: GraphMapper
) : GraphQLQueryResolver {

    fun human(rawName: String): Human? {
        val name = Name.parse(rawName)
        return graph.allV<Human> {
            has("name.first", name.first).apply {
                if (name.last != null) has("name.last", name.last)
            }
        }.toOptional().fetch()
    }
}
