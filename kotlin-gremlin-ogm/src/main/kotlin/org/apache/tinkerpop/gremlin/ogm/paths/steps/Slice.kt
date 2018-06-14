package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A path that filters out objects outside a given range.
 */
class SliceStep<TYPE>(private val range: LongRange) : Step.ToOptional<TYPE, TYPE>({
    it.traversal.range(range.start, range.endInclusive + 1)
})

fun <FROM, TO> Path.ToMany<FROM, TO>.slice(range: LongRange): Path.ToMany<FROM, TO> = to(SliceStep(range))
