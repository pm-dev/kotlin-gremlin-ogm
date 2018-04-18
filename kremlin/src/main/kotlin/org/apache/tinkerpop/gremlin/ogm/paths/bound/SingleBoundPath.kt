package org.apache.tinkerpop.gremlin.ogm.paths.bound

/**
 * A [SingleBoundPath] is a [BoundPath] that is bound to only 1 'from' object.
 */
interface SingleBoundPath<FROM : Any, TO> : BoundPath<FROM, TO> {

    /**
     * The object to start the [Path]'s traversal with
     */
    val from: FROM

    override val froms: Iterable<FROM> get() = listOf(from)

    /**
     * A [SingleBoundPath] that results in 0 or more 'TO' objects for each 'FROM' object
     * the path is traversed with.
     */
    interface ToMany<FROM : Any, TO> : SingleBoundPath<FROM, TO>, BoundPath.ToMany<FROM, TO>

    /**
     * A [SingleBoundPath] that results in exactly 1 'TO' objects for each 'FROM' object
     * the path is traversed with.
     */
    interface ToSingle<FROM : Any, TO> : SingleBoundPath<FROM, TO>, BoundPath.ToSingle<FROM, TO>

    /**
     * A [SingleBoundPath] that results in 0 or 1 'TO' objects for each 'FROM' object
     * the path is traversed with.
     */
    interface ToOptional<FROM : Any, TO> : SingleBoundPath<FROM, TO>, BoundPath.ToOptional<FROM, TO>
}
