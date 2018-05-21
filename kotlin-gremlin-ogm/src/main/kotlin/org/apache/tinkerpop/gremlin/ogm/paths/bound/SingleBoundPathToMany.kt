package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A [SingleBoundPath] that results in 0 or more 'IN' objects for each 'OUT' object
 * the path is traversed with.
 */
class SingleBoundPathToMany<OUT : Any, IN>(
        override val outV: OUT,
        override val path: Path.ToMany<OUT, IN>
) : SingleBoundPath.ToMany<OUT, IN>
