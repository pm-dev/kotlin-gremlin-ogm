package framework

import java.time.Instant

internal abstract class BaseVertex : BaseElement<Long>() {

    abstract val createdAt: Instant
}
