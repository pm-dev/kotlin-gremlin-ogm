package starwars.models

import org.apache.tinkerpop.gremlin.ogm.annotations.ID
import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import org.apache.tinkerpop.gremlin.ogm.annotations.Vertex
import java.time.Instant

@Vertex(label = "Droid")
internal class Droid(

        @ID
        id: Long? = null,

        @Property(key = "createdAt")
        createdAt: Instant,

        @Property(key = "name")
        name: Name,

        @Property(key = "appearsIn")
        appearsIn: Set<Episode>,

        @Property(key = "primaryFunction")
        val primaryFunction: String
) : Character(
        id = id,
        createdAt = createdAt,
        name = name,
        appearsIn = appearsIn
)
