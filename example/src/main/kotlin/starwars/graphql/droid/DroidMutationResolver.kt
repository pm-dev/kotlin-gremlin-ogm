package starwars.graphql.droid

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.relationships.`in`
import org.apache.tinkerpop.gremlin.ogm.relationships.bound.out
import org.springframework.stereotype.Component
import starwars.models.*
import java.time.Instant

@Component
class DroidMutationResolver(
        private val graph: GraphMapper
) : GraphQLMutationResolver {

    fun createDroid(
            name: String,
            primaryFunction: String,
            friendIds: Set<Long>,
            appearsIn: Set<Episode>): Droid {
        val nameParts = name.split(" ")
        val lastName = nameParts.subList(1, nameParts.size).joinToString(" ")
        val friends = graph.load<Character>(friendIds).filterNotNull()
        val droid = graph.saveV(Droid(
                name = Name(first = nameParts.first(), last = if (lastName.isEmpty()) null else lastName),
                appearsIn = appearsIn,
                createdAt = Instant.now(),
                primaryFunction = primaryFunction))
        graph.saveE(droid out Character.friends `in` friends)
        return droid
    }
}
