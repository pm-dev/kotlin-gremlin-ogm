package starwars.graphql.human

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.ogm.graphMapper
import org.apache.tinkerpop.gremlin.ogm.allV
import org.springframework.stereotype.Component
import starwars.models.Human
import starwars.models.Name

@Component
internal class HumanQueryResolver : GraphQLQueryResolver {

    fun human(rawName: String, env: DataFetchingEnvironment): Human? {
        val name = Name.parse(rawName)
        return env.graphMapper.allV<Human> {
            has("name.first", name.first).apply {
                if (name.last != null) has("name.last", name.last)
            }
        }.toOptional().fetch()
    }
}
