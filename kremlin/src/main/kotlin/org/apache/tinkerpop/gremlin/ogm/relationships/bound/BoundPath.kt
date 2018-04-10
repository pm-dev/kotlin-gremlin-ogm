package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.steps.Path

/**
 * A [BoundPath] encapsulates a traversable [Path] as well as the objects the path starts from
 */
interface BoundPath<FROM : Any, TO> {

    val froms: Iterable<FROM>

    val path: Path<FROM, TO>

    interface ToOne<FROM : Any, TO> : BoundPath<FROM, TO>

    interface ToMany<FROM : Any, TO> : BoundPath<FROM, TO> {

        override val path: Path.ToMany<FROM, TO>

        fun add(from: FROM): ToMany<FROM, TO>
        fun add(vararg from: FROM): ToMany<FROM, TO>
        fun add(from: Iterable<FROM>): ToMany<FROM, TO>
    }

    interface ToSingle<FROM : Any, TO> : ToOne<FROM, TO> {

        override val path: Path.ToSingle<FROM, TO>

        fun add(from: FROM): ToSingle<FROM, TO>
        fun add(vararg from: FROM): ToSingle<FROM, TO>
        fun add(from: Iterable<FROM>): ToSingle<FROM, TO>
    }

    interface ToOptional<FROM : Any, TO> : ToOne<FROM, TO> {

        override val path: Path.ToOptional<FROM, TO>

        fun add(from: FROM): ToOptional<FROM, TO>
        fun add(vararg from: FROM): ToOptional<FROM, TO>
        fun add(from: Iterable<FROM>): ToOptional<FROM, TO>
    }
}
