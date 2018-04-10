package org.apache.tinkerpop.gremlin.ogm.relationships

import org.apache.tinkerpop.gremlin.ogm.relationships.bound.*

/**
 * A Edge represents a FROM and TO vertex that are connected through a relationship.
 * Currently, Edges do not support their own properties, but that is a top priority of the next iteration
 * of this library.
 */
class Edge<FROM : Any, TO : Any>(val from: FROM, val to: TO, val relationship: Relationship<FROM, TO>) {
    val inverse: Edge<TO, FROM> get() = Edge(to, from, relationship.inverse)
}

infix fun <FROM : Any, TO : Any> SingleBoundRelationship.ToOne<FROM, TO>.`in`(to: TO): Edge<FROM, TO> = Edge(from = from, to = to, relationship = path)
infix fun <FROM : Any, TO : Any> SingleBoundRelationship.ToMany<FROM, TO>.`in`(to: TO): Edge<FROM, TO> = Edge(from = from, to = to, relationship = path)
infix fun <FROM : Any, TO : Any> SingleBoundRelationship.ToMany<FROM, TO>.`in`(tos: Iterable<TO>): List<Edge<FROM, TO>> = tos.map { to -> Edge(from = from, to = to, relationship = path) }
infix fun <FROM : Any, TO : Any> MultiBoundRelationship<FROM, TO>.`in`(to: TO): List<Edge<FROM, TO>> = froms.map { from -> Edge(from = from, to = to, relationship = path) }
