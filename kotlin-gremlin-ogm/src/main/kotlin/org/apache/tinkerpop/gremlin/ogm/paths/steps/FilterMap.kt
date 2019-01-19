package org.apache.tinkerpop.gremlin.ogm.paths.steps

/**
 * A step that maps the current object to a new object, or removes the current object if the map function returns null.
 */
internal class FilterMap<FROM, TO>(map: (FROM) -> TO?) : StepToOptional<FROM, TO>({ traverser ->
    traverser.traversal
            .map { map(it.get()) }
            .filter { it.get() != null }
            .map { it.get()!! }
})
