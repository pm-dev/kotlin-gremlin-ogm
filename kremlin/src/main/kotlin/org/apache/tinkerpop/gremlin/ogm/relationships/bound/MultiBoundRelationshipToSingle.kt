package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.Relationship

class MultiBoundRelationshipToSingle<FROM : Any, TO : Any>(
        override val froms: Iterable<FROM>,
        override val path: Relationship.ToSingle<FROM, TO>
) : MultiBoundRelationship.ToSingle<FROM, TO> {

    override fun add(from: FROM) = MultiBoundRelationshipToSingle(froms.plus(from), path)
    override fun add(vararg from: FROM) = MultiBoundRelationshipToSingle(froms.plus(from), path)
    override fun add(from: Iterable<FROM>) = MultiBoundRelationshipToSingle(froms.plus(from), path)
}

fun <FROM : Any, TO : Any> Relationship.ToSingle<FROM, TO>.from(froms: Iterable<FROM>) = MultiBoundRelationshipToSingle(froms, this)
fun <FROM : Any, TO : Any> Relationship.ToSingle<FROM, TO>.from(vararg froms: FROM) = MultiBoundRelationshipToSingle(froms.toList(), this)

infix fun <FROM : Any, TO : Any> Iterable<FROM>.out(relationship: Relationship.ToSingle<FROM, TO>) = MultiBoundRelationshipToSingle(froms = this, path = relationship)
