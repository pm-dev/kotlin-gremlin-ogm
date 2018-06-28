package starwars.models

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
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
    companion object {
        val friends = Relationship.symmetricManyToMany<Character>(name = "friends")
    }
}
