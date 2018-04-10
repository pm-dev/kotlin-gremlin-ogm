package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.Relationship

class MultiBoundRelationshipToMany<FROM : Any, TO : Any>(
        override val froms: Iterable<FROM>,
        override val path: Relationship.ToMany<FROM, TO>
) : MultiBoundRelationship.ToMany<FROM, TO> {

    override fun add(from: FROM) = MultiBoundRelationshipToMany(froms.plus(from), path)
    override fun add(vararg from: FROM) = MultiBoundRelationshipToMany(froms.plus(from), path)
    override fun add(from: Iterable<FROM>) = MultiBoundRelationshipToMany(froms.plus(from), path)
}

fun <FROM : Any, TO : Any> Relationship.ToMany<FROM, TO>.from(froms: Iterable<FROM>) = MultiBoundRelationshipToMany(froms, this)
fun <FROM : Any, TO : Any> Relationship.ToMany<FROM, TO>.from(vararg froms: FROM) = MultiBoundRelationshipToMany(froms.toList(), this)

infix fun <FROM : Any, TO : Any> Iterable<FROM>.out(relationship: Relationship.ToMany<FROM, TO>) = MultiBoundRelationshipToMany(froms = this, path = relationship)
