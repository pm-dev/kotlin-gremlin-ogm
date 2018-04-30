package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A [BoundPath] that results in exactly 1 object for each [from] object that
 * the traversed path starts with.
 */
class BoundPathToSingle<OUT : Any, IN>(
        override val outVs: Iterable<OUT>,
        override val path: Path.ToSingle<OUT, IN>
) : BoundPath.ToSingle<OUT, IN>
