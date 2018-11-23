package starwars.models

import org.apache.tinkerpop.gremlin.ogm.annotations.defaults.DefaultValue
import org.apache.tinkerpop.gremlin.ogm.annotations.Element
import org.apache.tinkerpop.gremlin.ogm.annotations.ID
import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import java.time.Instant
import java.util.function.Supplier

@Element(label = "Human")
internal class Human(

        @ID
        id: Long? = null,

        @Property(key = "createdAt")
        createdAt: Instant,

        @Property(key = "name")
        @DefaultValue(DefaultName::class)
        name: Name,

        @Property(key = "appearsIn")
        appearsIn: Set<Episode>,

        @Property(key = "homePlanet")
        val homePlanet: String?
) : Character(
        id = id,
        createdAt = createdAt,
        name = name,
        appearsIn = appearsIn
) {
    companion object {

        class DefaultName : Supplier<Name> {
            override fun get() = Name(first = "Unknown", last = "Name")
        }
    }
}

