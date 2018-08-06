package starwars.graphql.droid

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.ogm.graphMapper
import org.apache.tinkerpop.gremlin.ogm.allV
import org.springframework.stereotype.Component
import starwars.models.Droid
import starwars.models.Name

@Component
internal class DroidQueryResolver : GraphQLQueryResolver {

    fun droid(rawName: String, env: DataFetchingEnvironment): Droid? {
        val name = Name.parse(rawName)
        return env.graphMapper.allV<Droid> {
            has("name.first", name.first).apply {
                if (name.last != null) has("name.last", name.last)
            }
        }.toOptional().fetch()
    }
}
