package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.steps.Path

class MultiBoundPathToMany<FROM : Any, TO>(
        override val froms: Iterable<FROM>,
        override val path: Path.ToMany<FROM, TO>
) : MultiBoundPath.ToMany<FROM, TO> {

    override fun add(from: FROM) = MultiBoundPathToMany(froms.plus(from), path)
    override fun add(vararg from: FROM) = MultiBoundPathToMany(froms.plus(from), path)
    override fun add(from: Iterable<FROM>) = MultiBoundPathToMany(froms.plus(from), path)
}

fun <FROM : Any, TO> Path.ToMany<FROM, TO>.from(froms: Iterable<FROM>) = MultiBoundPathToMany(froms, this)
fun <FROM : Any, TO> Path.ToMany<FROM, TO>.from(vararg froms: FROM) = MultiBoundPathToMany(froms.toList(), this)

infix fun <FROM : Any, TO : Any> Iterable<FROM>.out(path: Path.ToMany<FROM, TO>) = MultiBoundPathToMany(froms = this, path = path)
