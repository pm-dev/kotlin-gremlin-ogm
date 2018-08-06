package starwars.graphql.droid

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.ogm.graphMapper
import org.apache.tinkerpop.gremlin.ogm.paths.bound.from
import org.apache.tinkerpop.gremlin.ogm.paths.bound.to
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
            env: DataFetchingEnvironment): Droid {
        val friends = env.graphMapper.V<Character>(friendIds).fetch()
        val droid = env.graphMapper.saveV(Droid(
                name = Name.parse(name),
                appearsIn = appearsIn,
                createdAt = Instant.now(),
                primaryFunction = primaryFunction))
        env.graphMapper.saveE(Character.friends from droid to friends)
//        graph.traversal.tx().commit() // Uncomment to save the droid
        return droid
    }
}
