package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A [BoundPath] that results in 0 or more objects for each [from] object that
 * the traversed path starts with.
 */
class BoundPathToMany<FROM : Any, TO>(
        override val froms: Iterable<FROM>,
        override val path: Path.ToMany<FROM, TO>
) : BoundPath.ToMany<FROM, TO>
