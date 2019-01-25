package starwars.models

import framework.BaseVertex
import org.apache.tinkerpop.gremlin.ogm.annotations.Element
import org.apache.tinkerpop.gremlin.ogm.annotations.ID
import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import org.apache.tinkerpop.gremlin.ogm.annotations.defaults.DefaultString
import org.apache.tinkerpop.gremlin.ogm.annotations.defaults.DefaultValue
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.SingleBoundStep
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec.ManyToManySymmetricEdgeSpec
import org.janusgraph.ogm.annotations.Indexed
import starwars.traversals.character.secondDegreeFriends
import starwars.traversals.human.twinSiblings
import java.time.Instant
import java.util.function.Supplier

internal sealed class Character : BaseVertex() {

    abstract val name: Name

    abstract val appearsIn: Set<Episode>

    val friends: SingleBoundStep.ToMany<Character, Character> get() = Companion.friends from this

    val secondDegreeFriends get() = Companion.secondDegreeFriends from this

    companion object {

        val friends = ManyToManySymmetricEdgeSpec<Character>(name = "friends")
    }
}

@Element(label = "Droid")
internal data class Droid(

        @ID
        override val id: Long? = null,

        @Property(key = "createdAt")
        override val createdAt: Instant,

        @Indexed
        @Property(key = "name")
        override val name: Name,

        @Property(key = "appearsIn")
        override val appearsIn: Set<Episode>,

        @Property(key = "primaryFunction")
        @DefaultString("Unknown Function")
        val primaryFunction: String
) : Character()

@Element(label = "Human")
internal data class Human(

        @ID
        override val id: Long? = null,

        @Property(key = "createdAt")
        override val createdAt: Instant,

        @Indexed
        @Property(key = "name")
        @DefaultValue(DefaultName::class)
        override val name: Name,

        @Property(key = "appearsIn")
        override val appearsIn: Set<Episode>,

        @Property(key = "homePlanet")
        val homePlanet: String?
) : Character() {

    val twins get() = Human.twinSiblings from this

    companion object {
        
        class DefaultName : Supplier<Name> {
            override fun get() = Name(given = "Unknown", surname = "Name")
        }
    }
}
