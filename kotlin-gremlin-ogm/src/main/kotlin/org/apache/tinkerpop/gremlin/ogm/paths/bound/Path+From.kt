package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship


/**
 * The 'from' function on a [Path] can be used to bind the path to 1 or more OUT objects
 */

infix fun <OUT : Any, IN> Path.ToMany<OUT, IN>.from(from: OUT) = SingleBoundPathToMany(from, this)
infix fun <OUT : Any, IN> Path.ToMany<OUT, IN>.from(froms: Iterable<OUT>) = BoundPathToMany(froms, this)
fun <OUT : Any, IN> Path.ToMany<OUT, IN>.from(vararg froms: OUT) = BoundPathToMany(froms.toList(), this)

infix fun <OUT : Any, IN> Path.ToOptional<OUT, IN>.from(from: OUT) = SingleBoundPathToOptional(from, this)
infix fun <OUT : Any, IN> Path.ToOptional<OUT, IN>.from(froms: Iterable<OUT>) = BoundPathToOptional(froms, this)
fun <OUT : Any, IN> Path.ToOptional<OUT, IN>.from(vararg froms: OUT) = BoundPathToOptional(froms.toList(), this)

infix fun <OUT : Any, IN> Path.ToSingle<OUT, IN>.from(from: OUT) = SingleBoundPathToSingle(from, this)
infix fun <OUT : Any, IN> Path.ToSingle<OUT, IN>.from(froms: Iterable<OUT>) = BoundPathToSingle(froms, this)
fun <OUT : Any, IN> Path.ToSingle<OUT, IN>.from(vararg froms: OUT) = BoundPathToSingle(froms.toList(), this)

infix fun <OUT : Any, IN : Any> Relationship.ToMany<OUT, IN>.from(from: OUT) = SingleBoundRelationshipToMany(from, this)
infix fun <OUT : Any, IN : Any> Relationship.ToMany<OUT, IN>.from(froms: Iterable<OUT>) = BoundRelationshipToMany(froms, this)
fun <OUT : Any, IN : Any> Relationship.ToMany<OUT, IN>.from(vararg froms: OUT) = BoundRelationshipToMany(froms.toList(), this)

infix fun <OUT : Any, IN : Any> Relationship.ToOptional<OUT, IN>.from(from: OUT) = SingleBoundRelationshipToOptional(from, this)
infix fun <OUT : Any, IN : Any> Relationship.ToOptional<OUT, IN>.from(froms: Iterable<OUT>) = BoundRelationshipToOptional(froms, this)
fun <OUT : Any, IN : Any> Relationship.ToOptional<OUT, IN>.from(vararg froms: OUT) = BoundRelationshipToOptional(froms.toList(), this)

infix fun <OUT : Any, IN : Any> Relationship.ToSingle<OUT, IN>.from(from: OUT) = SingleBoundRelationshipToSingle(from, this)
infix fun <OUT : Any, IN : Any> Relationship.ToSingle<OUT, IN>.from(froms: Iterable<OUT>) = BoundRelationshipToSingle(froms, this)
fun <OUT : Any, IN : Any> Relationship.ToSingle<OUT, IN>.from(vararg froms: OUT) = BoundRelationshipToSingle(froms.toList(), this)
