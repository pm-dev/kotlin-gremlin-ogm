package org.apache.tinkerpop.gremlin.ogm.paths.steps

/**
 * A step that maps the current object to zero or more new objects.
 */
internal class FlatMap<FROM, TO>(map: (FROM) -> Iterable<TO>) : StepToMany<FROM, TO>({ traverser ->
    traverser.traversal.flatMap { map(it.get()).iterator() }
})
