package org.apache.tinkerpop.gremlin.ogm.steps.path

import org.apache.tinkerpop.gremlin.ogm.steps.Step

internal data class PathToOptional<FROM, MIDDLE, TO>(
        override val first: Step.ToOne<FROM, MIDDLE>,
        override val last: Step.ToOne<MIDDLE, TO>
) : Path.ToOptional<FROM, MIDDLE, TO>
