package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.steps.Path

class SingleBoundPathToSingle<FROM : Any, TO>(
        override val from: FROM,
        override val path: Path.ToSingle<FROM, TO>
) : SingleBoundPath.ToSingle<FROM, TO> {

    override fun add(from: FROM) = MultiBoundPathToSingle(froms.plus(from), path)
    override fun add(vararg from: FROM) = MultiBoundPathToSingle(froms.plus(from), path)
    override fun add(from: Iterable<FROM>) = MultiBoundPathToSingle(froms.plus(from), path)
}

fun <FROM : Any, TO> Path.ToSingle<FROM, TO>.from(from: FROM) = SingleBoundPathToSingle(from, this)

infix fun <FROM : Any, TO : Any> FROM.out(path: Path.ToSingle<FROM, TO>) = SingleBoundPathToSingle(from = this, path = path)
