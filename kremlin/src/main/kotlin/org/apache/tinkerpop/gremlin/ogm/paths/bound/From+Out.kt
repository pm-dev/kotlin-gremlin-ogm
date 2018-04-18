package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * The 'out' function can be used to combine a 'FROM' vertex and a [Path] to create a [BoundPath]
 */

infix fun <FROM : Any, TO : Any> Iterable<FROM>.out(path: Path.ToMany<FROM, TO>) = BoundPathToMany(froms = this, path = path)
infix fun <FROM : Any, TO : Any> Iterable<FROM>.out(path: Path.ToOptional<FROM, TO>) = BoundPathToOptional(froms = this, path = path)
infix fun <FROM : Any, TO : Any> Iterable<FROM>.out(path: Path.ToSingle<FROM, TO>) = BoundPathToSingle(froms = this, path = path)

infix fun <FROM : Any, TO : Any> Iterable<FROM>.out(relationship: Relationship.ToMany<FROM, TO>) = BoundRelationshipToMany(froms = this, path = relationship)
infix fun <FROM : Any, TO : Any> Iterable<FROM>.out(relationship: Relationship.ToOptional<FROM, TO>) = BoundRelationshipToOptional(froms = this, path = relationship)
infix fun <FROM : Any, TO : Any> Iterable<FROM>.out(relationship: Relationship.ToSingle<FROM, TO>) = BoundRelationshipToSingle(froms = this, path = relationship)

infix fun <FROM : Any, TO : Any> FROM.out(path: Path.ToMany<FROM, TO>) = SingleBoundPathToMany(from = this, path = path)
infix fun <FROM : Any, TO : Any> FROM.out(path: Path.ToOptional<FROM, TO>) = SingleBoundPathToOptional(from = this, path = path)
infix fun <FROM : Any, TO : Any> FROM.out(path: Path.ToSingle<FROM, TO>) = SingleBoundPathToSingle(from = this, path = path)

infix fun <FROM : Any, TO : Any> FROM.out(relationship: Relationship.ToMany<FROM, TO>) = SingleBoundRelationshipToMany(from = this, path = relationship)
infix fun <FROM : Any, TO : Any> FROM.out(relationship: Relationship.ToOptional<FROM, TO>) = SingleBoundRelationshipToOptional(from = this, path = relationship)
infix fun <FROM : Any, TO : Any> FROM.out(relationship: Relationship.ToSingle<FROM, TO>) = SingleBoundRelationshipToSingle(from = this, path = relationship)
