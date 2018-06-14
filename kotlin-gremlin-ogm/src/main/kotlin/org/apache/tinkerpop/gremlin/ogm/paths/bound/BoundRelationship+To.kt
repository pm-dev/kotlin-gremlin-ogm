package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.BaseEdge

/**
 * The 'to' function on a [BoundRelationship] creates an edge (or multiple edges if the Relationship is
 * not single-bound) that can be saved to the graph.
 */

infix fun <FROM : Any, TO : Any> SingleBoundRelationshipToSingle<FROM, TO>.to(to: TO): BaseEdge<FROM, TO> = BaseEdge(from = from, to = to, relationship = path)
infix fun <FROM : Any, TO : Any> SingleBoundRelationshipToOptional<FROM, TO>.to(to: TO): BaseEdge<FROM, TO> = BaseEdge(from = from, to = to, relationship = path)
infix fun <FROM : Any, TO : Any> SingleBoundRelationshipToMany<FROM, TO>.to(to: TO): BaseEdge<FROM, TO> = BaseEdge(from = from, to = to, relationship = path)
infix fun <FROM : Any, TO : Any> SingleBoundRelationshipToMany<FROM, TO>.to(tos: Iterable<TO>): List<BaseEdge<FROM, TO>> = tos.map {  BaseEdge(from = from, to = it, relationship = path) }
infix fun <FROM : Any, TO : Any> BoundRelationshipToOptional<FROM, TO>.to(to: TO): List<BaseEdge<FROM, TO>> = froms.map { BaseEdge(from = it, to = to, relationship = path) }
infix fun <FROM : Any, TO : Any> BoundRelationshipToSingle<FROM, TO>.to(to: TO): List<BaseEdge<FROM, TO>> = froms.map { BaseEdge(from = it, to = to, relationship = path) }
