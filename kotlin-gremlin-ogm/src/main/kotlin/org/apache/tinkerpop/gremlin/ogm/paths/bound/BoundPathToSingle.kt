package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A [BoundPath] that results to exactly 1 object for each [from] object that
 * the traversed path starts with.
 */
class BoundPathToSingle<FROM : Vertex, TO>(
        override val froms: List<FROM>,
        override val path: Path.ToSingle<FROM, TO>
) : BoundPath.ToSingle<FROM, TO>
