package starwars.models

import java.time.Instant

internal abstract class BaseVertex(
        id: Long?,
        val createdAt: Instant
) : BaseElement<Long>(id)
