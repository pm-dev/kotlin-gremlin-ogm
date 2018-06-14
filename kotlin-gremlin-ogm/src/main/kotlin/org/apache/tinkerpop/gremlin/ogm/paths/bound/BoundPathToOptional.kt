package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A [BoundPath] that results to 0 or 1 object for each [from] object that
 * the traversed path starts with.
 */
class BoundPathToOptional<FROM : Any, TO>(
        override val froms: Iterable<FROM>,
        override val path: Path.ToOptional<FROM, TO>
) : BoundPath.ToOptional<FROM, TO>
