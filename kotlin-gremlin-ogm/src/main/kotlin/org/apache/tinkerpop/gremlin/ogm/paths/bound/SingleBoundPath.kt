package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex

/**
 * A [SingleBoundPath] is a [BoundPath] that is bound to only 1 'from' object.
 */
interface SingleBoundPath<FROM : Vertex, TO> : BoundPath<FROM, TO> {

    /**
     * The object to start the [SingleBoundPath]'s traversal with
     */
    val from: FROM

    override val froms: Iterable<FROM> get() = listOf(from)

    /**
     * A [SingleBoundPath] that results to 0 or more 'TO' objects for each 'FROM' object
     * the path is traversed with.
     */
    interface ToMany<FROM : Vertex, TO> : SingleBoundPath<FROM, TO>, BoundPath.ToMany<FROM, TO>

    /**
     * A [SingleBoundPath] that results to exactly 1 'TO' objects for each 'FROM' object
     * the path is traversed with.
     */
    interface ToSingle<FROM : Vertex, TO> : SingleBoundPath<FROM, TO>, BoundPath.ToSingle<FROM, TO>

    /**
     * A [SingleBoundPath] that results to 0 or 1 'TO' objects for each 'FROM' object
     * the path is traversed with.
     */
    interface ToOptional<FROM : Vertex, TO> : SingleBoundPath<FROM, TO>, BoundPath.ToOptional<FROM, TO>
}
