@file:Suppress("unused")

package starwars.graphql.human

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.ogm.fetch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.tinkerpop.gremlin.ogm.allV
import org.springframework.stereotype.Component
import starwars.models.Human
import starwars.models.Name
import java.util.concurrent.CompletableFuture

@ExperimentalCoroutinesApi
@Component
internal class HumanQueryResolver : GraphQLQueryResolver {

    fun human(rawName: String, env: DataFetchingEnvironment): CompletableFuture<Human?> = env.fetch {
        val name = Name.parse(raw = rawName)
        graphMapper.allV<Human> {
            has("name.given", name.given).apply {
                if (name.surname != null) has("name.surname", name.surname)
            }
        }.singleOrNull()
    }
}
