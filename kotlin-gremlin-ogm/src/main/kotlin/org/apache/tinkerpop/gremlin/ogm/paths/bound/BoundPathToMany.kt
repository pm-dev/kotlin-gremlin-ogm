package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A [BoundPath] that results to 0 or more objects for each [from] object that
 * the traversed path starts with.
 */
class BoundPathToMany<FROM : Vertex, TO>(
        override val froms: List<FROM>,
        override val path: Path.ToMany<FROM, TO>
) : BoundPath.ToMany<FROM, TO>
