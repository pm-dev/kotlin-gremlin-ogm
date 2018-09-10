package starwars.models

import org.apache.tinkerpop.gremlin.ogm.annotations.Element
import org.apache.tinkerpop.gremlin.ogm.annotations.ID
import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import org.apache.tinkerpop.gremlin.ogm.annotations.defaults.DefaultString
import java.time.Instant

@Element(label = "Droid")
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
        @DefaultString("Unknown Function")
        val primaryFunction: String
) : Character(
        id = id,
        createdAt = createdAt,
        name = name,
        appearsIn = appearsIn
)
