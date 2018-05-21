package org.apache.tinkerpop.gremlin.ogm.paths.bound

/**
 * A [SingleBoundPath] is a [BoundPath] that is bound to only 1 'from' object.
 */
interface SingleBoundPath<OUT : Any, IN> : BoundPath<OUT, IN> {

    /**
     * The object to start the [Path]'s traversal with
     */
    val outV: OUT

    override val outVs: Iterable<OUT> get() = listOf(outV)

    /**
     * A [SingleBoundPath] that results in 0 or more 'IN' objects for each 'OUT' object
     * the path is traversed with.
     */
    interface ToMany<OUT : Any, IN> : SingleBoundPath<OUT, IN>, BoundPath.ToMany<OUT, IN>

    /**
     * A [SingleBoundPath] that results in exactly 1 'IN' objects for each 'OUT' object
     * the path is traversed with.
     */
    interface ToSingle<OUT : Any, IN> : SingleBoundPath<OUT, IN>, BoundPath.ToSingle<OUT, IN>

    /**
     * A [SingleBoundPath] that results in 0 or 1 'IN' objects for each 'OUT' object
     * the path is traversed with.
     */
    interface ToOptional<OUT : Any, IN> : SingleBoundPath<OUT, IN>, BoundPath.ToOptional<OUT, IN>
}
