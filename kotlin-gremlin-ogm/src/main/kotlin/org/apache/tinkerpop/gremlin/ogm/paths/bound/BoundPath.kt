package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A [BoundPath] encapsulates a [Path] plus the [OUT] objects to start the path's traversal with
 */
interface BoundPath<OUT : Any, IN> {

    /**
     * The objects to start traversing [path] with.
     */
    val outVs: Iterable<OUT>

    /**
     * A path through the graph to traverse through using the [from] objects
     */
    val path: Path<OUT, IN>

    /**
     * A [BoundPath] that results in a optional or non-optional (aka single) object
     * for each [from] object that the path is traversed with.
     */
    interface ToOne<OUT : Any, IN> : BoundPath<OUT, IN>

    /**
     * A [BoundPath] that results in 0 or more objects for each [from] object that
     * the traversed path starts with.
     */
    interface ToMany<OUT : Any, IN> : BoundPath<OUT, IN> {

        override val path: Path.ToMany<OUT, IN>
    }

    /**
     * A [BoundPath] that results in a non-optional object
     * for each [from] object that the path is traversed with.
     */
    interface ToSingle<OUT : Any, IN> : ToOne<OUT, IN> {

        override val path: Path.ToSingle<OUT, IN>
    }

    /**
     * A [BoundPath] that results in an optional object
     * for each [from] object that the path is traversed with.
     */
    interface ToOptional<OUT : Any, IN> : ToOne<OUT, IN> {

        override val path: Path.ToOptional<OUT, IN>
    }
}
