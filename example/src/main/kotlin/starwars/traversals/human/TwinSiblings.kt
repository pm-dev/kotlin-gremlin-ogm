package starwars.traversals.human

import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.steps.EdgeStep
import org.apache.tinkerpop.gremlin.ogm.paths.steps.filter
import org.apache.tinkerpop.gremlin.ogm.paths.steps.map
import starwars.models.Human
import starwars.models.Sibling
import starwars.models.Sibling.Companion.siblings

internal val Human.Companion.twinSiblings
    get() : Path.ToMany<Human, Human> =
        EdgeStep.ToMany<Human, Human, Sibling>(siblings)
                .filter(Sibling::twins)
                .map(Sibling::to)
