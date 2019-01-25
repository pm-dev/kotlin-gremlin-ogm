package org.apache.tinkerpop.gremlin.ogm.steps.path

import org.apache.tinkerpop.gremlin.ogm.steps.Step

internal data class PathToMany<FROM, MIDDLE, TO>(
        override val first: Step<FROM, MIDDLE>,
        override val last: Step<MIDDLE, TO>
) : Path.ToMany<FROM, MIDDLE, TO>
