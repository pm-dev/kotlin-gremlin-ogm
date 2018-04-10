package starwars

import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.relationships.`in`
import org.apache.tinkerpop.gremlin.ogm.relationships.bound.out
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import starwars.models.*
import java.time.Instant
import java.util.*

@Component
open class StarwarsGraphLoader(
        private val graph: GraphMapper
) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {

        val now = Instant.now()

        val lukeSkywalker = graph.saveV(Human(
                name = Name(first = "Luke", last ="Skywalker"),
                homePlanet = "Tatooine",
                appearsIn = EnumSet.of(Episode.NEW_HOPE, Episode.JEDI, Episode.EMPIRE),
                createdAt = now))

        val darthVader = graph.saveV(Human(
                name = Name(first = "Darth", last ="Vader"),
                homePlanet = "Tatooine",
                appearsIn = EnumSet.of(Episode.NEW_HOPE, Episode.JEDI, Episode.EMPIRE),
                createdAt = now))

        val hanSolo = graph.saveV(Human(
                name = Name(first = "Han", last ="Solo"),
                appearsIn = EnumSet.of(Episode.NEW_HOPE, Episode.JEDI, Episode.EMPIRE),
                homePlanet = null,
                createdAt = now))

        val leiaOrgana = graph.saveV(Human(
                name = Name(first = "Leia", last ="Organa"),
                homePlanet = "Alderaan",
                appearsIn = EnumSet.of(Episode.NEW_HOPE, Episode.JEDI, Episode.EMPIRE),
                createdAt = now))

        val wilhuffTarkin = graph.saveV(Human(
                name = Name(first = "Wilhuff", last ="Tarkin"),
                appearsIn = EnumSet.of(Episode.NEW_HOPE),
                homePlanet = null,
                createdAt = now))

        val c3po = graph.saveV(Droid(
                name = Name(first = "C-3PO"),
                appearsIn = EnumSet.of(Episode.NEW_HOPE, Episode.JEDI, Episode.EMPIRE),
                primaryFunction = "Protocol",
                createdAt = now))

        val aretoo = graph.saveV(Droid(
                name = Name(first = "R2-D2"),
                appearsIn = EnumSet.of(Episode.NEW_HOPE, Episode.JEDI, Episode.EMPIRE),
                primaryFunction = "Astromech",
                createdAt = now))

        graph.saveE(lukeSkywalker out Character.friends `in` listOf(hanSolo, leiaOrgana, c3po, aretoo))
        graph.saveE(darthVader out Character.friends `in` listOf(wilhuffTarkin))
        graph.saveE(c3po out Character.friends `in` aretoo)
        graph.saveE(hanSolo out Character.friends `in` listOf(leiaOrgana, aretoo))

        println("Loaded Starwars Graph")
    }
}
