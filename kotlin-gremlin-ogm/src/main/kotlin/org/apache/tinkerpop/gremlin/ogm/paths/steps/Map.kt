package org.apache.tinkerpop.gremlin.ogm.paths.steps

/**
 * A step that maps the current object to a new object.
 */
internal class Map<FROM, TO>(map: (FROM) -> TO) : StepToSingle<FROM, TO>({ traverser ->
    traverser.traversal.map { map(it.get()) }
})
