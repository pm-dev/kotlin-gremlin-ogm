package org.apache.tinkerpop.gremlin.ogm.relationships.steps

/**
 * A step that filters out duplicate objects at the current location in the traversal.
 */
class Dedup<TYPE> : Step.ToSingle<TYPE, TYPE>({ it.traversal.dedup() })

fun <FROM, TO> Path.ToMany<FROM, TO>.dedup(): Path.ToMany<FROM, TO> = to(Dedup())
