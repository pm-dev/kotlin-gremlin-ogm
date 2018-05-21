package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

internal interface LinkedPath<OUT, MIDDLE, IN> : Path<OUT, IN> {

    val first: Path<OUT, MIDDLE>

    val second: Path<MIDDLE, IN>

    override fun path() = first.path() + second.path()

    override fun invoke(from: StepTraverser<OUT>): GraphTraversal<*, IN> =
            second(StepTraverser(first(from), from.vertexMapper, from.edgeMapper))

    class ToSingle<OUT, MIDDLE, IN>(
            override val first: Path.ToSingle<OUT, MIDDLE>,
            override val second: Path.ToSingle<MIDDLE, IN>
    ) : LinkedPath<OUT, MIDDLE, IN>, Path.ToSingle<OUT, IN>

    class ToOptional<OUT, MIDDLE, IN>(
            override val first: Path.ToOne<OUT, MIDDLE>,
            override val second: Path.ToOne<MIDDLE, IN>
    ) : LinkedPath<OUT, MIDDLE, IN>, Path.ToOptional<OUT, IN>

    class ToMany<OUT, MIDDLE, IN>(
            override val first: Path<OUT, MIDDLE>,
            override val second: Path<MIDDLE, IN>
    ) : LinkedPath<OUT, MIDDLE, IN>, Path.ToMany<OUT, IN>
}

fun <OUT, IN, NEXT> Path.ToMany<OUT, IN>.to(next: Path<IN, NEXT>): Path.ToMany<OUT, NEXT> = LinkedPath.ToMany(first = this, second = next)

fun <OUT, IN, NEXT> Path.ToOptional<OUT, IN>.to(next: Path.ToOne<IN, NEXT>): Path.ToOptional<OUT, NEXT> = LinkedPath.ToOptional(first = this, second = next)
fun <OUT, IN, NEXT> Path.ToOptional<OUT, IN>.to(next: Path.ToMany<IN, NEXT>): Path.ToMany<OUT, NEXT> = LinkedPath.ToMany(first = this, second = next)

fun <OUT, IN, NEXT> Path.ToSingle<OUT, IN>.to(next: Path.ToSingle<IN, NEXT>): Path.ToSingle<OUT, NEXT> = LinkedPath.ToSingle(first = this, second = next)
fun <OUT, IN, NEXT> Path.ToSingle<OUT, IN>.to(next: Path.ToOptional<IN, NEXT>): Path.ToOptional<OUT, NEXT> = LinkedPath.ToOptional(first = this, second = next)
fun <OUT, IN, NEXT> Path.ToSingle<OUT, IN>.to(next: Path.ToMany<IN, NEXT>): Path.ToMany<OUT, NEXT> = LinkedPath.ToMany(first = this, second = next)
