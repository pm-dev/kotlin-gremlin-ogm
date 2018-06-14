package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A step that filters out duplicate objects at the current location to the traversal.
 */
class Dedup<TYPE> : Step.ToSingle<TYPE, TYPE>({ it.traversal.dedup() })

fun <FROM, TO> Path.ToMany<FROM, TO>.dedup(): Path.ToMany<FROM, TO> = to(Dedup())
