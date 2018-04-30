package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A [BoundPath] that results in 0 or more objects for each [from] object that
 * the traversed path starts with.
 */
class BoundPathToMany<OUT : Any, IN>(
        override val outVs: Iterable<OUT>,
        override val path: Path.ToMany<OUT, IN>
) : BoundPath.ToMany<OUT, IN>
