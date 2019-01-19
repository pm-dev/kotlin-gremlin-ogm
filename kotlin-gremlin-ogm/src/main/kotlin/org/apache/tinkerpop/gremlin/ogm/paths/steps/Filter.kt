package org.apache.tinkerpop.gremlin.ogm.paths.steps

/**
 * A step that doesn't advance to a new type but simply removes a subset of objects from the g based on
 * a predicate.
 */
internal class Filter<TYPE>(predicate: (TYPE) -> Boolean) : StepToOptional<TYPE, TYPE>({
    it.traversal.filter { traverser -> predicate(traverser.get()) }
})
