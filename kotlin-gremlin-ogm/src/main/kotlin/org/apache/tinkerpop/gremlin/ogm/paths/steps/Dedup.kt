package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A step that filters out duplicate objects at the current location in the traversal.
 */
class Dedup<TYPE> : Step.ToSingle<TYPE, TYPE>({ it.traversal.dedup() })

fun <OUT, IN> Path.ToMany<OUT, IN>.dedup(): Path.ToMany<OUT, IN> = to(Dedup())
