package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A path that filters out objects outside a given range.
 */
class SliceStep<TYPE>(private val range: LongRange) : Step.ToOptional<TYPE, TYPE>({
    it.traversal.range(range.start, range.endInclusive + 1)
})

fun <OUT, IN> Path.ToMany<OUT, IN>.slice(range: LongRange): Path.ToMany<OUT, IN> = to(SliceStep(range))
