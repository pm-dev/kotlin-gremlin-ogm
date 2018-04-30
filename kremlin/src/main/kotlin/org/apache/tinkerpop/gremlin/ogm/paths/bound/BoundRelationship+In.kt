package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.BaseEdge

/**
 * The 'in' function on a [BoundRelationship] creates an edge (or multiple edges if the Relationship is
 * not single-bound) that can be saved to the graph.
 */

infix fun <OUT : Any, IN : Any> SingleBoundRelationshipToSingle<OUT, IN>.`in`(to: IN): BaseEdge<OUT, IN> = BaseEdge(outV = outV, inV = to, relationship = path)
infix fun <OUT : Any, IN : Any> SingleBoundRelationshipToOptional<OUT, IN>.`in`(to: IN): BaseEdge<OUT, IN> = BaseEdge(outV = outV, inV = to, relationship = path)
infix fun <OUT : Any, IN : Any> SingleBoundRelationshipToMany<OUT, IN>.`in`(to: IN): BaseEdge<OUT, IN> = BaseEdge(outV = outV, inV = to, relationship = path)
infix fun <OUT : Any, IN : Any> SingleBoundRelationshipToMany<OUT, IN>.`in`(tos: Iterable<IN>): List<BaseEdge<OUT, IN>> = tos.map {  BaseEdge(outV = outV, inV = it, relationship = path) }
infix fun <OUT : Any, IN : Any> BoundRelationshipToOptional<OUT, IN>.`in`(to: IN): List<BaseEdge<OUT, IN>> = outVs.map { BaseEdge(outV = it, inV = to, relationship = path) }
infix fun <OUT : Any, IN : Any> BoundRelationshipToSingle<OUT, IN>.`in`(to: IN): List<BaseEdge<OUT, IN>> = outVs.map { BaseEdge(outV = it, inV = to, relationship = path) }
