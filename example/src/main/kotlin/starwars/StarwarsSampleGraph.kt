package starwars

import org.apache.tinkerpop.gremlin.ogm.paths.bound.from
import org.apache.tinkerpop.gremlin.ogm.paths.bound.to
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import starwars.models.*
import starwars.models.Character.Companion.friends
import java.time.Instant
import java.util.*

@Component
internal open class StarwarsSampleGraph(
        private val graphSupplier: StarwarsGraphMapperSupplier
) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {

        val graph = graphSupplier.get()

        val now = Instant.now()

        val lukeSkywalker = graph.saveV(Human(
                name = Name(given = "Luke", surname = "Skywalker"),
                homePlanet = "Tatooine",
                appearsIn = EnumSet.of(Episode.NEW_HOPE, Episode.JEDI, Episode.EMPIRE),
                createdAt = now))

        val darthVader = graph.saveV(Human(
                name = Name(given = "Darth", surname = "Vader"),
                homePlanet = "Tatooine",
                appearsIn = EnumSet.of(Episode.NEW_HOPE, Episode.JEDI, Episode.EMPIRE),
                createdAt = now))

        val hanSolo = graph.saveV(Human(
                name = Name(given = "Han", surname = "Solo"),
                appearsIn = EnumSet.of(Episode.NEW_HOPE, Episode.JEDI, Episode.EMPIRE),
                homePlanet = null,
                createdAt = now))

        val leiaOrgana = graph.saveV(Human(
                name = Name(given = "Leia", surname = "Organa"),
                homePlanet = "Alderaan",
                appearsIn = EnumSet.of(Episode.NEW_HOPE, Episode.JEDI, Episode.EMPIRE),
                createdAt = now))

        val wilhuffTarkin = graph.saveV(Human(
                name = Name(given = "Wilhuff", surname = "Tarkin"),
                appearsIn = EnumSet.of(Episode.NEW_HOPE),
                homePlanet = null,
                createdAt = now))

        val c3po = graph.saveV(Droid(
                name = Name(given = "C-3PO"),
                appearsIn = EnumSet.of(Episode.NEW_HOPE, Episode.JEDI, Episode.EMPIRE),
                primaryFunction = "Protocol",
                createdAt = now))

        val aretoo = graph.saveV(Droid(
                name = Name(given = "R2-D2"),
                appearsIn = EnumSet.of(Episode.NEW_HOPE, Episode.JEDI, Episode.EMPIRE),
                primaryFunction = "Astromech",
                createdAt = now))

        graph.saveE(friends from lukeSkywalker to listOf(hanSolo, leiaOrgana, c3po, aretoo))
        graph.saveE(friends from darthVader to listOf(wilhuffTarkin))
        graph.saveE(friends from c3po to aretoo)
        graph.saveE(friends from hanSolo to listOf(leiaOrgana, aretoo))
        graph.saveE(Sibling(from = lukeSkywalker, to = leiaOrgana, twins = true))
        graph.traversal.tx().commit()
        println("Loaded Starwars Graph")
    }
}
