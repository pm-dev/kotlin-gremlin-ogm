package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Edge

/**
 * The 'in' function on a [BoundRelationship] creates an edge (or multiple edges if the Relationship is
 * not single-bound) that can be saved to the graph.
 */

infix fun <FROM : Any, TO : Any> SingleBoundRelationshipToSingle<FROM, TO>.`in`(to: TO): Edge<FROM, TO> = Edge(from = from, to = to, relationship = path)
infix fun <FROM : Any, TO : Any> SingleBoundRelationshipToOptional<FROM, TO>.`in`(to: TO): Edge<FROM, TO> = Edge(from = from, to = to, relationship = path)
infix fun <FROM : Any, TO : Any> SingleBoundRelationshipToMany<FROM, TO>.`in`(to: TO): Edge<FROM, TO> = Edge(from = from, to = to, relationship = path)
infix fun <FROM : Any, TO : Any> SingleBoundRelationshipToMany<FROM, TO>.`in`(tos: Iterable<TO>): List<Edge<FROM, TO>> = tos.map { to -> Edge(from = from, to = to, relationship = path) }
infix fun <FROM : Any, TO : Any> BoundRelationshipToOptional<FROM, TO>.`in`(to: TO): List<Edge<FROM, TO>> = froms.map { from -> Edge(from = from, to = to, relationship = path) }
infix fun <FROM : Any, TO : Any> BoundRelationshipToSingle<FROM, TO>.`in`(to: TO): List<Edge<FROM, TO>> = froms.map { from -> Edge(from = from, to = to, relationship = path) }
