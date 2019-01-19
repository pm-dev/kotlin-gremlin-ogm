package org.apache.tinkerpop.gremlin.ogm.paths.steps

/**
 * A path that filters out objects outside a given range.
 */
internal class Slice<TYPE>(range: LongRange) : StepToOptional<TYPE, TYPE>({
    it.traversal.range(range.start, range.endInclusive + 1)
})

