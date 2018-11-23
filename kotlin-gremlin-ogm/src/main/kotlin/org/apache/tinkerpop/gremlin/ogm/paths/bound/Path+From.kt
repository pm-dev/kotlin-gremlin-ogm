package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship


/**
 * The 'from' function on a [Path] can be used to bind the path to 1 or more FROM objects
 */

infix fun <FROM : Vertex, TO> Path.ToMany<FROM, TO>.from(from: FROM) = SingleBoundPathToMany(from, this)

infix fun <FROM : Vertex, TO> Path.ToMany<FROM, TO>.from(froms: Iterable<FROM>) = BoundPathToMany(froms, this)
fun <FROM : Vertex, TO> Path.ToMany<FROM, TO>.from(vararg froms: FROM) = BoundPathToMany(froms.toList(), this)

infix fun <FROM : Vertex, TO> Path.ToOptional<FROM, TO>.from(from: FROM) = SingleBoundPathToOptional(from, this)
infix fun <FROM : Vertex, TO> Path.ToOptional<FROM, TO>.from(froms: Iterable<FROM>) = BoundPathToOptional(froms, this)
fun <FROM : Vertex, TO> Path.ToOptional<FROM, TO>.from(vararg froms: FROM) = BoundPathToOptional(froms.toList(), this)

infix fun <FROM : Vertex, TO> Path.ToSingle<FROM, TO>.from(from: FROM) = SingleBoundPathToSingle(from, this)
infix fun <FROM : Vertex, TO> Path.ToSingle<FROM, TO>.from(froms: Iterable<FROM>) = BoundPathToSingle(froms, this)
fun <FROM : Vertex, TO> Path.ToSingle<FROM, TO>.from(vararg froms: FROM) = BoundPathToSingle(froms.toList(), this)

infix fun <FROM : Vertex, TO : Vertex> Relationship.ToMany<FROM, TO>.from(from: FROM) = SingleBoundRelationshipToMany(from, this)
infix fun <FROM : Vertex, TO : Vertex> Relationship.ToMany<FROM, TO>.from(froms: Iterable<FROM>) = BoundRelationshipToMany(froms, this)
fun <FROM : Vertex, TO : Vertex> Relationship.ToMany<FROM, TO>.from(vararg froms: FROM) = BoundRelationshipToMany(froms.toList(), this)

infix fun <FROM : Vertex, TO : Vertex> Relationship.ToOptional<FROM, TO>.from(from: FROM) = SingleBoundRelationshipToOptional(from, this)
infix fun <FROM : Vertex, TO : Vertex> Relationship.ToOptional<FROM, TO>.from(froms: Iterable<FROM>) = BoundRelationshipToOptional(froms, this)
fun <FROM : Vertex, TO : Vertex> Relationship.ToOptional<FROM, TO>.from(vararg froms: FROM) = BoundRelationshipToOptional(froms.toList(), this)

infix fun <FROM : Vertex, TO : Vertex> Relationship.ToSingle<FROM, TO>.from(from: FROM) = SingleBoundRelationshipToSingle(from, this)
infix fun <FROM : Vertex, TO : Vertex> Relationship.ToSingle<FROM, TO>.from(froms: Iterable<FROM>) = BoundRelationshipToSingle(froms, this)
fun <FROM : Vertex, TO : Vertex> Relationship.ToSingle<FROM, TO>.from(vararg froms: FROM) = BoundRelationshipToSingle(froms.toList(), this)
