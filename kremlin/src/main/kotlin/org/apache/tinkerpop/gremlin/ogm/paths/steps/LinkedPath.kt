package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

internal interface LinkedPath<FROM, MIDDLE, TO> : Path<FROM, TO> {

    val first: Path<FROM, MIDDLE>

    val second: Path<MIDDLE, TO>

    override fun path() = first.path() + second.path()

    override fun invoke(from: StepTraverser<FROM>): GraphTraversal<*, TO> =
            second(StepTraverser(first(from), from.mapper))

    class ToSingle<FROM, MIDDLE, TO>(
            override val first: Path.ToSingle<FROM, MIDDLE>,
            override val second: Path.ToSingle<MIDDLE, TO>
    ) : LinkedPath<FROM, MIDDLE, TO>, Path.ToSingle<FROM, TO>

    class ToOptional<FROM, MIDDLE, TO>(
            override val first: Path.ToOne<FROM, MIDDLE>,
            override val second: Path.ToOne<MIDDLE, TO>
    ) : LinkedPath<FROM, MIDDLE, TO>, Path.ToOptional<FROM, TO>

    class ToMany<FROM, MIDDLE, TO>(
            override val first: Path<FROM, MIDDLE>,
            override val second: Path<MIDDLE, TO>
    ) : LinkedPath<FROM, MIDDLE, TO>, Path.ToMany<FROM, TO>
}

fun <FROM, TO, NEXT> Path.ToMany<FROM, TO>.to(next: Path<TO, NEXT>): Path.ToMany<FROM, NEXT> = LinkedPath.ToMany(first = this, second = next)

fun <FROM, TO, NEXT> Path.ToOptional<FROM, TO>.to(next: Path.ToOne<TO, NEXT>): Path.ToOptional<FROM, NEXT> = LinkedPath.ToOptional(first = this, second = next)
fun <FROM, TO, NEXT> Path.ToOptional<FROM, TO>.to(next: Path.ToMany<TO, NEXT>): Path.ToMany<FROM, NEXT> = LinkedPath.ToMany(first = this, second = next)

fun <FROM, TO, NEXT> Path.ToSingle<FROM, TO>.to(next: Path.ToSingle<TO, NEXT>): Path.ToSingle<FROM, NEXT> = LinkedPath.ToSingle(first = this, second = next)
fun <FROM, TO, NEXT> Path.ToSingle<FROM, TO>.to(next: Path.ToOptional<TO, NEXT>): Path.ToOptional<FROM, NEXT> = LinkedPath.ToOptional(first = this, second = next)
fun <FROM, TO, NEXT> Path.ToSingle<FROM, TO>.to(next: Path.ToMany<TO, NEXT>): Path.ToMany<FROM, NEXT> = LinkedPath.ToMany(first = this, second = next)
