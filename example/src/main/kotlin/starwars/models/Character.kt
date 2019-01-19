package starwars.models

import framework.BaseVertex
import io.reactivex.Observable
import kotlinx.coroutines.rx2.await
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.paths.bound.bind
import org.apache.tinkerpop.gremlin.ogm.paths.bound.from
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.edgespec.ManyToManySymmetricEdgeSpec
import org.apache.tinkerpop.gremlin.ogm.rx.rx
import org.apache.tinkerpop.gremlin.ogm.traversals.SingleBoundMapper
import org.janusgraph.ogm.annotations.Indexed
import java.time.Instant

internal abstract class Character(

        id: Long?,

        createdAt: Instant,

        @Indexed
        val name: Name,

        val appearsIn: Set<Episode>
) : BaseVertex(
        id = id,
        createdAt = createdAt
) {

    fun friends(mapper: GraphMapper): List<Character> = mapper bind this traverse friends

    fun friendsMovies(mapper: GraphMapper): List<Episode> =
            friends(mapper).flatMap(Character::appearsIn)

    fun secondDegreeFriends(mapper: GraphMapper): List<Character> {
        val firstDegreeFriends = friends(mapper)
        val secondDegreeFriends = mapper.traversal(friends from firstDegreeFriends).traverse()
        return secondDegreeFriends.values.asSequence()
                .flatten()
                .distinct()
                .filter { !secondDegreeFriends.containsKey(it) }
                .toList()
    }

    companion object {
        val friends = ManyToManySymmetricEdgeSpec<Character>(name = "friends")
    }
}

internal val SingleBoundMapper<Character>.friends: Observable<Character>
    get() = traversal(Character.friends).rx()

internal val SingleBoundMapper<Character>.friendsMovies: Observable<Episode>
    get() = friends
            .flatMap { Observable.fromIterable(it.appearsIn) }
            .distinct()

internal suspend fun SingleBoundMapper<Character>.secondDegreeFriends(): Sequence<Character> = friends
        .toList()
        .flatMap { friends -> (mapper bind friends traversal Character.friends).rx() }
        .map { friendsToTheirFriends ->
            friendsToTheirFriends
                    .values
                    .asSequence()
                    .flatten()
                    .distinct()
                    .filter { !friendsToTheirFriends.containsKey(it) }
        }
        .await()

