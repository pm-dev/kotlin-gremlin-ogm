package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.Relationship

class MultiBoundRelationshipToOptional<FROM : Any, TO : Any>(
        override val froms: Iterable<FROM>,
        override val path: Relationship.ToOptional<FROM, TO>
) : MultiBoundRelationship.ToOptional<FROM, TO> {

    override fun add(from: FROM) = MultiBoundRelationshipToOptional(froms.plus(from), path)
    override fun add(vararg from: FROM) = MultiBoundRelationshipToOptional(froms.plus(from), path)
    override fun add(from: Iterable<FROM>) = MultiBoundRelationshipToOptional(froms.plus(from), path)
}

fun <FROM : Any, TO : Any> Relationship.ToOptional<FROM, TO>.from(froms: Iterable<FROM>) = MultiBoundRelationshipToOptional(froms, this)
fun <FROM : Any, TO : Any> Relationship.ToOptional<FROM, TO>.from(vararg froms: FROM) = MultiBoundRelationshipToOptional(froms.toList(), this)

infix fun <FROM : Any, TO : Any> Iterable<FROM>.out(relationship: Relationship.ToOptional<FROM, TO>) = MultiBoundRelationshipToOptional(froms = this, path = relationship)
