package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.steps.Path

class MultiBoundPathToSingle<FROM : Any, TO>(
        override val froms: Iterable<FROM>,
        override val path: Path.ToSingle<FROM, TO>
) : MultiBoundPath.ToSingle<FROM, TO> {

    override fun add(from: FROM) = MultiBoundPathToSingle(froms.plus(from), path)
    override fun add(vararg from: FROM) = MultiBoundPathToSingle(froms.plus(from), path)
    override fun add(from: Iterable<FROM>) = MultiBoundPathToSingle(froms.plus(from), path)
}

fun <FROM : Any, TO> Path.ToSingle<FROM, TO>.from(froms: Iterable<FROM>) = MultiBoundPathToSingle(froms, this)
fun <FROM : Any, TO> Path.ToSingle<FROM, TO>.from(vararg froms: FROM) = MultiBoundPathToSingle(froms.toList(), this)


infix fun <FROM : Any, TO : Any> Iterable<FROM>.out(path: Path.ToSingle<FROM, TO>) = MultiBoundPathToSingle(froms = this, path = path)
