@file:Suppress("unused")

package starwars.graphql.character

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.ogm.fetch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import org.apache.tinkerpop.gremlin.ogm.allV
import org.springframework.stereotype.Component
import starwars.models.Character
import starwars.models.Name
import java.util.concurrent.CompletableFuture

@ExperimentalCoroutinesApi
@Component
internal class CharacterQueryResolver : GraphQLQueryResolver {

    fun hero(env: DataFetchingEnvironment): CompletableFuture<Character> = GlobalScope.future {
        character(rawName = "Luke Skywalker", env = env).await()!!
    }

    fun character(rawName: String, env: DataFetchingEnvironment): CompletableFuture<Character?> = env.fetch {
        // JanusGraph will warn that this query requires iterating over all vertices.
        // This is because abstract Vertex classes are queried by union-ing queries of their base classes
        // Need to find a fix for this.
        val name = Name.parse(raw = rawName)
        graphMapper.allV<Character> {
            has("name.given", name.given).apply {
                if (name.surname != null) has("name.surname", name.surname)
            }
        }.singleOrNull()
    }
}

