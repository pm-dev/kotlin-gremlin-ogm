package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A [SingleBoundPath] that results in 0 or 1 'IN' objects for each 'OUT' object
 * the path is traversed with.
 */
class SingleBoundPathToOptional<OUT : Any, IN>(
        override val outV: OUT,
        override val path: Path.ToOptional<OUT, IN>
) : SingleBoundPath.ToOptional<OUT, IN>
