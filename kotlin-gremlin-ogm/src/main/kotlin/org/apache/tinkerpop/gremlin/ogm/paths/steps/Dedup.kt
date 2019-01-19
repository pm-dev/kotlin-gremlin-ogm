package org.apache.tinkerpop.gremlin.ogm.paths.steps

/**
 * A step that filters out duplicate objects at the current location to the g.
 */
internal class Dedup<TYPE> : StepToSingle<TYPE, TYPE>({ it.traversal.dedup() })
