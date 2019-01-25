@file:Suppress("unused")

package starwars.graphql.droid

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.ogm.mutate
import org.springframework.stereotype.Component
import starwars.models.Character
import starwars.models.Droid
import starwars.models.Episode
import starwars.models.Name
import java.time.Instant

@Component
internal class DroidMutationResolver : GraphQLMutationResolver {

    fun createDroid(
            name: String,
            primaryFunction: String,
            friendIds: Set<Long>,
            appearsIn: Set<Episode>,
            env: DataFetchingEnvironment
    ): Droid = env.mutate {
        val friends = V<Character>(ids = friendIds)
        val droid = saveV(vertex = Droid(
                name = Name.parse(raw = name),
                appearsIn = appearsIn,
                createdAt = Instant.now(),
                primaryFunction = primaryFunction))
        saveE(edges = Character.friends from droid to friends)
        droid
    }
}
