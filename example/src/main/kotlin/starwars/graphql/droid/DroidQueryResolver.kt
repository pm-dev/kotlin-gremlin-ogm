package starwars.graphql.droid

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.allV
import org.springframework.stereotype.Component
import starwars.models.Droid
import starwars.models.Name


@Component
internal class DroidQueryResolver(
        private val graph: GraphMapper
): GraphQLQueryResolver {

    fun droid(rawName: String): Droid? {
        val name = Name.parse(rawName)
        return graph.allV<Droid> {
            has("name.first", name.first).apply {
                if (name.last != null) has("name.last", name.last)
            }
        }.toOptional().fetch()
    }
}
