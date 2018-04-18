package starwars.models

import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import java.time.Instant

internal abstract class Character(
        id: Long?,
        createdAt: Instant,

        @property:Property(key = "name")
        val name: Name,

        @property:Property(key = "appearsIn")
        val appearsIn: Set<Episode>
) : Node(
        id = id,
        createdAt = createdAt
) {
    companion object {
        val friends = Relationship.symmetricManyToMany<Character>(name = "friends")
    }
}
