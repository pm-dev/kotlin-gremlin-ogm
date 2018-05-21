package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A [SingleBoundPath] that results in exactly 1 'IN' objects for each 'OUT' object
 * the path is traversed with.
 */
class SingleBoundPathToSingle<OUT : Any, IN>(
        override val outV: OUT,
        override val path: Path.ToSingle<OUT, IN>
) : SingleBoundPath.ToSingle<OUT, IN>
