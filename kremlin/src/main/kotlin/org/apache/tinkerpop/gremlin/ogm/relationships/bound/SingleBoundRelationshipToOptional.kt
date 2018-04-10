package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.Relationship

class SingleBoundRelationshipToOptional<FROM : Any, TO : Any>(
        override val from: FROM,
        override val path: Relationship.ToOptional<FROM, TO>
) : SingleBoundRelationship.ToOptional<FROM, TO> {

    override fun add(from: FROM) = MultiBoundRelationshipToOptional(froms.plus(from), path)
    override fun add(vararg from: FROM) = MultiBoundRelationshipToOptional(froms.plus(from), path)
    override fun add(from: Iterable<FROM>) = MultiBoundRelationshipToOptional(froms.plus(from), path)
}

fun <FROM : Any, TO : Any> Relationship.ToOptional<FROM, TO>.from(from: FROM) = SingleBoundRelationshipToOptional(from, this)

infix fun <FROM : Any, TO : Any> FROM.out(relationship: Relationship.ToOptional<FROM, TO>) = SingleBoundRelationshipToOptional(from = this, path = relationship)
