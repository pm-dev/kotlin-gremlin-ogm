package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A [SingleBoundPath] that results to exactly 1 'TO' objects for each 'FROM' object
 * the path is traversed with.
 */
class SingleBoundPathToSingle<FROM : Any, TO>(
        override val from: FROM,
        override val path: Path.ToSingle<FROM, TO>
) : SingleBoundPath.ToSingle<FROM, TO>
