@file:Suppress("unused")

package starwars.graphql.droid

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.ogm.fetch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.tinkerpop.gremlin.ogm.allV
import org.springframework.stereotype.Component
import starwars.models.Droid
import starwars.models.Name
import java.util.concurrent.CompletableFuture

@ExperimentalCoroutinesApi
@Component
internal class DroidQueryResolver : GraphQLQueryResolver {

    fun droid(rawName: String, env: DataFetchingEnvironment): CompletableFuture<Droid?> = env.fetch {
        val name = Name.parse(raw = rawName)
        graphMapper.allV<Droid> {
            has("name.given", name.given).apply {
                if (name.surname != null) has("name.surname", name.surname)
            }
        }.singleOrNull()
    }
}
