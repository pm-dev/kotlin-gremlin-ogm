package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A [BoundPath] encapsulates a [Path] plus the [FROM] objects to start the path's traversal with
 */
interface BoundPath<FROM : Any, TO> {

    /**
     * The objects to start traversing [path] with.
     */
    val froms: Iterable<FROM>

    /**
     * A path through the graph to traverse through using the [from] objects
     */
    val path: Path<FROM, TO>

    /**
     * A [BoundPath] that results to a optional or non-optional (aka single) object
     * for each [from] object that the path is traversed with.
     */
    interface ToOne<FROM : Any, TO> : BoundPath<FROM, TO>

    /**
     * A [BoundPath] that results to 0 or more objects for each [from] object that
     * the traversed path starts with.
     */
    interface ToMany<FROM : Any, TO> : BoundPath<FROM, TO> {

        override val path: Path.ToMany<FROM, TO>
    }

    /**
     * A [BoundPath] that results to a non-optional object
     * for each [from] object that the path is traversed with.
     */
    interface ToSingle<FROM : Any, TO> : ToOne<FROM, TO> {

        override val path: Path.ToSingle<FROM, TO>
    }

    /**
     * A [BoundPath] that results to an optional object
     * for each [from] object that the path is traversed with.
     */
    interface ToOptional<FROM : Any, TO> : ToOne<FROM, TO> {

        override val path: Path.ToOptional<FROM, TO>
    }
}
