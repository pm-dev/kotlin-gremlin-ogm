package starwars.traversals.human

import org.apache.tinkerpop.gremlin.ogm.paths.bound.from
import org.apache.tinkerpop.gremlin.ogm.paths.steps.EdgeStep
import org.apache.tinkerpop.gremlin.ogm.paths.steps.filter
import org.apache.tinkerpop.gremlin.ogm.paths.steps.map
import starwars.models.Human
import starwars.models.Sibling
import starwars.models.Sibling.Companion.siblings

internal fun Human.toTwinSiblings() =
        EdgeStep.ToMany<Human, Human, Sibling>(siblings).filter {
            it.twins
        }.map { it.inV } from this
