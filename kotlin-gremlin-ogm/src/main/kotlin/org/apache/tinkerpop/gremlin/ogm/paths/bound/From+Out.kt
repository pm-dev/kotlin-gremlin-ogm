package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * The 'out' function can be used to combine a 'OUT' vertex and a [Path] to create a [BoundPath]
 */


infix fun <OUT : Any, IN : Any> Iterable<OUT>.out(path: Path.ToMany<OUT, IN>) = BoundPathToMany(outVs = this, path = path)
infix fun <OUT : Any, IN : Any> Iterable<OUT>.out(path: Path.ToOptional<OUT, IN>) = BoundPathToOptional(outVs = this, path = path)
infix fun <OUT : Any, IN : Any> Iterable<OUT>.out(path: Path.ToSingle<OUT, IN>) = BoundPathToSingle(outVs = this, path = path)

infix fun <OUT : Any, IN : Any> Iterable<OUT>.out(relationship: Relationship.ToMany<OUT, IN>) = BoundRelationshipToMany(outVs = this, path = relationship)
infix fun <OUT : Any, IN : Any> Iterable<OUT>.out(relationship: Relationship.ToOptional<OUT, IN>) = BoundRelationshipToOptional(outVs = this, path = relationship)
infix fun <OUT : Any, IN : Any> Iterable<OUT>.out(relationship: Relationship.ToSingle<OUT, IN>) = BoundRelationshipToSingle(outVs = this, path = relationship)

infix fun <OUT : Any, IN : Any> OUT.out(path: Path.ToMany<OUT, IN>) = SingleBoundPathToMany(outV = this, path = path)
infix fun <OUT : Any, IN : Any> OUT.out(path: Path.ToOptional<OUT, IN>) = SingleBoundPathToOptional(outV = this, path = path)
infix fun <OUT : Any, IN : Any> OUT.out(path: Path.ToSingle<OUT, IN>) = SingleBoundPathToSingle(outV = this, path = path)

infix fun <OUT : Any, IN : Any> OUT.out(relationship: Relationship.ToMany<OUT, IN>) = SingleBoundRelationshipToMany(outV = this, path = relationship)
infix fun <OUT : Any, IN : Any> OUT.out(relationship: Relationship.ToOptional<OUT, IN>) = SingleBoundRelationshipToOptional(outV = this, path = relationship)
infix fun <OUT : Any, IN : Any> OUT.out(relationship: Relationship.ToSingle<OUT, IN>) = SingleBoundRelationshipToSingle(outV = this, path = relationship)


infix fun <OUT : Any, IN : Any> OUT.outE(relationship: Relationship.ToSingle<OUT, IN>) = SingleBoundRelationshipToSingle(outV = this, path = relationship)


