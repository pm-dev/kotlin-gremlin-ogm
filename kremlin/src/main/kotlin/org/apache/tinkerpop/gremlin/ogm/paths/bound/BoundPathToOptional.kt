package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A [BoundPath] that results in 0 or 1 object for each [from] object that
 * the traversed path starts with.
 */
class BoundPathToOptional<OUT : Any, IN>(
        override val outVs: Iterable<OUT>,
        override val path: Path.ToOptional<OUT, IN>
) : BoundPath.ToOptional<OUT, IN>
