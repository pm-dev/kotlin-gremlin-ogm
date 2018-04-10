package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.steps.Path

class MultiBoundPathToOptional<FROM : Any, TO>(
        override val froms: Iterable<FROM>,
        override val path: Path.ToOptional<FROM, TO>
) : MultiBoundPath.ToOptional<FROM, TO> {

    override fun add(from: FROM) = MultiBoundPathToOptional(froms.plus(from), path)
    override fun add(vararg from: FROM) = MultiBoundPathToOptional(froms.plus(from), path)
    override fun add(from: Iterable<FROM>) = MultiBoundPathToOptional(froms.plus(from), path)
}

fun <FROM : Any, TO> Path.ToOptional<FROM, TO>.from(froms: Iterable<FROM>) = MultiBoundPathToOptional(froms, this)
fun <FROM : Any, TO> Path.ToOptional<FROM, TO>.from(vararg froms: FROM) = MultiBoundPathToOptional(froms.toList(), this)

infix fun <FROM : Any, TO : Any> Iterable<FROM>.out(path: Path.ToOptional<FROM, TO>) = MultiBoundPathToOptional(froms = this, path = path)
