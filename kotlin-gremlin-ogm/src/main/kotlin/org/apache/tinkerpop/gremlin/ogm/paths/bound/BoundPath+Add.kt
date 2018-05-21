package org.apache.tinkerpop.gremlin.ogm.paths.bound

/**
 * The 'add' function on a [BoundPath] allows callers to append an additional object to start
 * the [Path]'s traversal from.
 */

fun <OUT : Any, IN> BoundPathToMany<OUT, IN>.add(outV: OUT) = BoundPathToMany(outVs.plus(outV), path)
fun <OUT : Any, IN> BoundPathToMany<OUT, IN>.add(vararg outV: OUT) = BoundPathToMany(outVs.plus(outV), path)
fun <OUT : Any, IN> BoundPathToMany<OUT, IN>.add(outV: Iterable<OUT>) = BoundPathToMany(outVs.plus(outV), path)

fun <OUT : Any, IN> BoundPathToOptional<OUT, IN>.add(outV: OUT) = BoundPathToOptional(outVs.plus(outV), path)
fun <OUT : Any, IN> BoundPathToOptional<OUT, IN>.add(vararg outV: OUT) = BoundPathToOptional(outVs.plus(outV), path)
fun <OUT : Any, IN> BoundPathToOptional<OUT, IN>.add(outV: Iterable<OUT>) = BoundPathToOptional(outVs.plus(outV), path)

fun <OUT : Any, IN> BoundPathToSingle<OUT, IN>.add(outV: OUT) = BoundPathToSingle(outVs.plus(outV), path)
fun <OUT : Any, IN> BoundPathToSingle<OUT, IN>.add(vararg outV: OUT) = BoundPathToSingle(outVs.plus(outV), path)
fun <OUT : Any, IN> BoundPathToSingle<OUT, IN>.add(outV: Iterable<OUT>) = BoundPathToSingle(outVs.plus(outV), path)

fun <OUT : Any, IN : Any> BoundRelationshipToMany<OUT, IN>.add(outV: OUT) = BoundRelationshipToMany(outVs.plus(outV), path)
fun <OUT : Any, IN : Any> BoundRelationshipToMany<OUT, IN>.add(vararg outV: OUT) = BoundRelationshipToMany(outVs.plus(outV), path)
fun <OUT : Any, IN : Any> BoundRelationshipToMany<OUT, IN>.add(outV: Iterable<OUT>) = BoundRelationshipToMany(outVs.plus(outV), path)

fun <OUT : Any, IN : Any> BoundRelationshipToOptional<OUT, IN>.add(outV: OUT) = BoundRelationshipToOptional(outVs.plus(outV), path)
fun <OUT : Any, IN : Any> BoundRelationshipToOptional<OUT, IN>.add(vararg outV: OUT) = BoundRelationshipToOptional(outVs.plus(outV), path)
fun <OUT : Any, IN : Any> BoundRelationshipToOptional<OUT, IN>.add(outV: Iterable<OUT>) = BoundRelationshipToOptional(outVs.plus(outV), path)

fun <OUT : Any, IN : Any> BoundRelationshipToSingle<OUT, IN>.add(outV: OUT) = BoundRelationshipToSingle(outVs.plus(outV), path)
fun <OUT : Any, IN : Any> BoundRelationshipToSingle<OUT, IN>.add(vararg outV: OUT) = BoundRelationshipToSingle(outVs.plus(outV), path)
fun <OUT : Any, IN : Any> BoundRelationshipToSingle<OUT, IN>.add(outV: Iterable<OUT>) = BoundRelationshipToSingle(outVs.plus(outV), path)

fun <OUT : Any, IN> SingleBoundPathToMany<OUT, IN>.add(outV: OUT) = BoundPathToMany(outVs.plus(outV), path)
fun <OUT : Any, IN> SingleBoundPathToMany<OUT, IN>.add(vararg outV: OUT) = BoundPathToMany(outVs.plus(outV), path)
fun <OUT : Any, IN> SingleBoundPathToMany<OUT, IN>.add(outV: Iterable<OUT>) = BoundPathToMany(outVs.plus(outV), path)

fun <OUT : Any, IN> SingleBoundPathToOptional<OUT, IN>.add(outV: OUT) = BoundPathToOptional(outVs.plus(outV), path)
fun <OUT : Any, IN> SingleBoundPathToOptional<OUT, IN>.add(vararg outV: OUT) = BoundPathToOptional(outVs.plus(outV), path)
fun <OUT : Any, IN> SingleBoundPathToOptional<OUT, IN>.add(outV: Iterable<OUT>) = BoundPathToOptional(outVs.plus(outV), path)

fun <OUT : Any, IN> SingleBoundPathToSingle<OUT, IN>.add(outV: OUT) = BoundPathToSingle(outVs.plus(outV), path)
fun <OUT : Any, IN> SingleBoundPathToSingle<OUT, IN>.add(vararg outV: OUT) = BoundPathToSingle(outVs.plus(outV), path)
fun <OUT : Any, IN> SingleBoundPathToSingle<OUT, IN>.add(outV: Iterable<OUT>) = BoundPathToSingle(outVs.plus(outV), path)

fun <OUT : Any, IN : Any> SingleBoundRelationshipToMany<OUT, IN>.add(outV: OUT) = BoundRelationshipToMany(outVs.plus(outV), path)
fun <OUT : Any, IN : Any> SingleBoundRelationshipToMany<OUT, IN>.add(vararg outV: OUT) = BoundRelationshipToMany(outVs.plus(outV), path)
fun <OUT : Any, IN : Any> SingleBoundRelationshipToMany<OUT, IN>.add(outV: Iterable<OUT>) = BoundRelationshipToMany(outVs.plus(outV), path)

fun <OUT : Any, IN : Any> SingleBoundRelationshipToOptional<OUT, IN>.add(outV: OUT) = BoundRelationshipToOptional(outVs.plus(outV), path)
fun <OUT : Any, IN : Any> SingleBoundRelationshipToOptional<OUT, IN>.add(vararg outV: OUT) = BoundRelationshipToOptional(outVs.plus(outV), path)
fun <OUT : Any, IN : Any> SingleBoundRelationshipToOptional<OUT, IN>.add(outV: Iterable<OUT>) = BoundRelationshipToOptional(outVs.plus(outV), path)

fun <OUT : Any, IN : Any> SingleBoundRelationshipToSingle<OUT, IN>.add(outV: OUT) = BoundRelationshipToSingle(outVs.plus(outV), path)
fun <OUT : Any, IN : Any> SingleBoundRelationshipToSingle<OUT, IN>.add(vararg outV: OUT) = BoundRelationshipToSingle(outVs.plus(outV), path)
fun <OUT : Any, IN : Any> SingleBoundRelationshipToSingle<OUT, IN>.add(outV: Iterable<OUT>) = BoundRelationshipToSingle(outVs.plus(outV), path)
