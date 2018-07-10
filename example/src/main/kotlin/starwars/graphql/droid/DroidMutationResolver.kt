package starwars.graphql.droid

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.paths.bound.from
import org.apache.tinkerpop.gremlin.ogm.paths.bound.to
import org.springframework.stereotype.Component
import starwars.models.Character
import starwars.models.Droid
import starwars.models.Episode
import starwars.models.Name
import java.time.Instant

@Component
internal class DroidMutationResolver(
        private val graph: GraphMapper
) : GraphQLMutationResolver {
    
    fun createDroid(
            name: String,
            primaryFunction: String,
            friendIds: Set<Long>,
            appearsIn: Set<Episode>): Droid {
        val friends = graph.V<Character>(friendIds).fetch()
        val droid = graph.saveV(Droid(
                name = Name.parse(name),
                appearsIn = appearsIn,
                createdAt = Instant.now(),
                primaryFunction = primaryFunction))
        graph.saveE(Character.friends from droid to friends)
//        graph.g.tx().commit() // Uncomment to save the droid
        return droid
    }
}
