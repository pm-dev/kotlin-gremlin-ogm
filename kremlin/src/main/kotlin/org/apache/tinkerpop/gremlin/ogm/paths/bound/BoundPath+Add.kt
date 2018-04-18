package org.apache.tinkerpop.gremlin.ogm.paths.bound

/**
 * The 'add' function on a [BoundPath] allows callers to append an additional object to start
 * the [Path]'s traversal from.
 */

fun <FROM : Any, TO> BoundPathToMany<FROM, TO>.add(from: FROM) = BoundPathToMany(froms.plus(from), path)
fun <FROM : Any, TO> BoundPathToMany<FROM, TO>.add(vararg from: FROM) = BoundPathToMany(froms.plus(from), path)
fun <FROM : Any, TO> BoundPathToMany<FROM, TO>.add(from: Iterable<FROM>) = BoundPathToMany(froms.plus(from), path)

fun <FROM : Any, TO> BoundPathToOptional<FROM, TO>.add(from: FROM) = BoundPathToOptional(froms.plus(from), path)
fun <FROM : Any, TO> BoundPathToOptional<FROM, TO>.add(vararg from: FROM) = BoundPathToOptional(froms.plus(from), path)
fun <FROM : Any, TO> BoundPathToOptional<FROM, TO>.add(from: Iterable<FROM>) = BoundPathToOptional(froms.plus(from), path)

fun <FROM : Any, TO> BoundPathToSingle<FROM, TO>.add(from: FROM) = BoundPathToSingle(froms.plus(from), path)
fun <FROM : Any, TO> BoundPathToSingle<FROM, TO>.add(vararg from: FROM) = BoundPathToSingle(froms.plus(from), path)
fun <FROM : Any, TO> BoundPathToSingle<FROM, TO>.add(from: Iterable<FROM>) = BoundPathToSingle(froms.plus(from), path)

fun <FROM : Any, TO : Any> BoundRelationshipToMany<FROM, TO>.add(from: FROM) = BoundRelationshipToMany(froms.plus(from), path)
fun <FROM : Any, TO : Any> BoundRelationshipToMany<FROM, TO>.add(vararg from: FROM) = BoundRelationshipToMany(froms.plus(from), path)
fun <FROM : Any, TO : Any> BoundRelationshipToMany<FROM, TO>.add(from: Iterable<FROM>) = BoundRelationshipToMany(froms.plus(from), path)

fun <FROM : Any, TO : Any> BoundRelationshipToOptional<FROM, TO>.add(from: FROM) = BoundRelationshipToOptional(froms.plus(from), path)
fun <FROM : Any, TO : Any> BoundRelationshipToOptional<FROM, TO>.add(vararg from: FROM) = BoundRelationshipToOptional(froms.plus(from), path)
fun <FROM : Any, TO : Any> BoundRelationshipToOptional<FROM, TO>.add(from: Iterable<FROM>) = BoundRelationshipToOptional(froms.plus(from), path)

fun <FROM : Any, TO : Any> BoundRelationshipToSingle<FROM, TO>.add(from: FROM) = BoundRelationshipToSingle(froms.plus(from), path)
fun <FROM : Any, TO : Any> BoundRelationshipToSingle<FROM, TO>.add(vararg from: FROM) = BoundRelationshipToSingle(froms.plus(from), path)
fun <FROM : Any, TO : Any> BoundRelationshipToSingle<FROM, TO>.add(from: Iterable<FROM>) = BoundRelationshipToSingle(froms.plus(from), path)

fun <FROM : Any, TO> SingleBoundPathToMany<FROM, TO>.add(from: FROM) = BoundPathToMany(froms.plus(from), path)
fun <FROM : Any, TO> SingleBoundPathToMany<FROM, TO>.add(vararg from: FROM) = BoundPathToMany(froms.plus(from), path)
fun <FROM : Any, TO> SingleBoundPathToMany<FROM, TO>.add(from: Iterable<FROM>) = BoundPathToMany(froms.plus(from), path)

fun <FROM : Any, TO> SingleBoundPathToOptional<FROM, TO>.add(from: FROM) = BoundPathToOptional(froms.plus(from), path)
fun <FROM : Any, TO> SingleBoundPathToOptional<FROM, TO>.add(vararg from: FROM) = BoundPathToOptional(froms.plus(from), path)
fun <FROM : Any, TO> SingleBoundPathToOptional<FROM, TO>.add(from: Iterable<FROM>) = BoundPathToOptional(froms.plus(from), path)

fun <FROM : Any, TO> SingleBoundPathToSingle<FROM, TO>.add(from: FROM) = BoundPathToSingle(froms.plus(from), path)
fun <FROM : Any, TO> SingleBoundPathToSingle<FROM, TO>.add(vararg from: FROM) = BoundPathToSingle(froms.plus(from), path)
fun <FROM : Any, TO> SingleBoundPathToSingle<FROM, TO>.add(from: Iterable<FROM>) = BoundPathToSingle(froms.plus(from), path)

fun <FROM : Any, TO : Any> SingleBoundRelationshipToMany<FROM, TO>.add(from: FROM) = BoundRelationshipToMany(froms.plus(from), path)
fun <FROM : Any, TO : Any> SingleBoundRelationshipToMany<FROM, TO>.add(vararg from: FROM) = BoundRelationshipToMany(froms.plus(from), path)
fun <FROM : Any, TO : Any> SingleBoundRelationshipToMany<FROM, TO>.add(from: Iterable<FROM>) = BoundRelationshipToMany(froms.plus(from), path)

fun <FROM : Any, TO : Any> SingleBoundRelationshipToOptional<FROM, TO>.add(from: FROM) = BoundRelationshipToOptional(froms.plus(from), path)
fun <FROM : Any, TO : Any> SingleBoundRelationshipToOptional<FROM, TO>.add(vararg from: FROM) = BoundRelationshipToOptional(froms.plus(from), path)
fun <FROM : Any, TO : Any> SingleBoundRelationshipToOptional<FROM, TO>.add(from: Iterable<FROM>) = BoundRelationshipToOptional(froms.plus(from), path)

fun <FROM : Any, TO : Any> SingleBoundRelationshipToSingle<FROM, TO>.add(from: FROM) = BoundRelationshipToSingle(froms.plus(from), path)
fun <FROM : Any, TO : Any> SingleBoundRelationshipToSingle<FROM, TO>.add(vararg from: FROM) = BoundRelationshipToSingle(froms.plus(from), path)
fun <FROM : Any, TO : Any> SingleBoundRelationshipToSingle<FROM, TO>.add(from: Iterable<FROM>) = BoundRelationshipToSingle(froms.plus(from), path)
