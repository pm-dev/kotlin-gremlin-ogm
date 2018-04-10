package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.steps.Path

class SingleBoundPathToMany<FROM : Any, TO>(
        override val from: FROM,
        override val path: Path.ToMany<FROM, TO>
) : SingleBoundPath.ToMany<FROM, TO> {

    override fun add(from: FROM) = MultiBoundPathToMany(froms.plus(from), path)
    override fun add(vararg from: FROM) = MultiBoundPathToMany(froms.plus(from), path)
    override fun add(from: Iterable<FROM>) = MultiBoundPathToMany(froms.plus(from), path)
}

fun <FROM : Any, TO> Path.ToMany<FROM, TO>.from(from: FROM) = SingleBoundPathToMany(from, this)

infix fun <FROM : Any, TO : Any> FROM.out(path: Path.ToMany<FROM, TO>) = SingleBoundPathToMany(from = this, path = path)
