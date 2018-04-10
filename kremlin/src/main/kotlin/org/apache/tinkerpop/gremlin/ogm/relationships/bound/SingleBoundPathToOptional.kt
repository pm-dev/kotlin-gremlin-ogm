package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.steps.Path

class SingleBoundPathToOptional<FROM : Any, TO>(
        override val from: FROM,
        override val path: Path.ToOptional<FROM, TO>
) : SingleBoundPath.ToOptional<FROM, TO> {

    override fun add(from: FROM) = MultiBoundPathToOptional(froms.plus(from), path)
    override fun add(vararg from: FROM) = MultiBoundPathToOptional(froms.plus(from), path)
    override fun add(from: Iterable<FROM>) = MultiBoundPathToOptional(froms.plus(from), path)
}

fun <FROM : Any, TO> Path.ToOptional<FROM, TO>.from(from: FROM) = SingleBoundPathToOptional(from, this)

infix fun <FROM : Any, TO : Any> FROM.out(path: Path.ToOptional<FROM, TO>) = SingleBoundPathToOptional(from = this, path = path)
