package org.apache.tinkerpop.gremlin.ogm.paths.steps.paths

import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step

internal data class PathToOptional<FROM, MIDDLE, TO>(
        override val first: Step.ToOne<FROM, MIDDLE>,
        override val last: Step.ToOne<MIDDLE, TO>
) : Path.ToOptional<FROM, MIDDLE, TO>
