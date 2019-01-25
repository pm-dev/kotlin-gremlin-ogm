package starwars.traversals.human

import org.apache.tinkerpop.gremlin.ogm.steps.edgestep.EdgeStepToMany
import starwars.models.Human
import starwars.models.Sibling
import starwars.models.Sibling.Companion.siblings

internal val Human.Companion.twinSiblings
    get() = EdgeStepToMany<Human, Human, Sibling>(siblings)
            .filter(Sibling::twins)
            .map(Sibling::to)
