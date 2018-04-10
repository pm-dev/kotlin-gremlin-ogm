package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.Relationship

class SingleBoundRelationshipToMany<FROM : Any, TO : Any>(
        override val from: FROM,
        override val path: Relationship.ToMany<FROM, TO>
) : SingleBoundRelationship.ToMany<FROM, TO> {

    override fun add(from: FROM) = MultiBoundRelationshipToMany(froms.plus(from), path)
    override fun add(vararg from: FROM) = MultiBoundRelationshipToMany(froms.plus(from), path)
    override fun add(from: Iterable<FROM>) = MultiBoundRelationshipToMany(froms.plus(from), path)
}

fun <FROM : Any, TO : Any> Relationship.ToMany<FROM, TO>.from(from: FROM) = SingleBoundRelationshipToMany(from, this)

infix fun <FROM : Any, TO : Any> FROM.out(relationship: Relationship.ToMany<FROM, TO>) = SingleBoundRelationshipToMany(from = this, path = relationship)
