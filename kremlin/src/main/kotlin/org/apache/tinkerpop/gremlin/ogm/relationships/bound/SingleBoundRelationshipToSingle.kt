package org.apache.tinkerpop.gremlin.ogm.relationships.bound

import org.apache.tinkerpop.gremlin.ogm.relationships.Relationship

class SingleBoundRelationshipToSingle<FROM : Any, TO : Any>(
        override val from: FROM,
        override val path: Relationship.ToSingle<FROM, TO>
) : SingleBoundRelationship.ToSingle<FROM, TO> {

    override fun add(from: FROM) = MultiBoundRelationshipToSingle(froms.plus(from), path)
    override fun add(vararg from: FROM) = MultiBoundRelationshipToSingle(froms.plus(from), path)
    override fun add(from: Iterable<FROM>) = MultiBoundRelationshipToSingle(froms.plus(from), path)
}

fun <FROM : Any, TO : Any> Relationship.ToSingle<FROM, TO>.from(from: FROM) = SingleBoundRelationshipToSingle(from, this)

infix fun <FROM : Any, TO : Any> FROM.out(relationship: Relationship.ToSingle<FROM, TO>) = SingleBoundRelationshipToSingle(from = this, path = relationship)
