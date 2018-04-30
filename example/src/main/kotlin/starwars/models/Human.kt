package starwars.models

import org.apache.tinkerpop.gremlin.ogm.annotations.ID
import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import org.apache.tinkerpop.gremlin.ogm.annotations.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import java.time.Instant

@Vertex(label = "Human")
internal class Human(

        @ID
        id: Long? = null,

        @Property(key = "createdAt")
        createdAt: Instant,

        @Property(key = "name")
        name: Name,

        @Property(key = "appearsIn")
        appearsIn: Set<Episode>,

        @param:Property(key = "homePlanet")
        @property:Property(key = "homePlanet")
        val homePlanet: String?
) : Character(
        id = id,
        createdAt = createdAt,
        name = name,
        appearsIn = appearsIn
)

