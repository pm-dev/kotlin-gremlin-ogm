package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.BasicEdge
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex

/**
 * The 'to' function on a [BoundRelationship] creates an edge (or multiple edges if the Relationship is
 * not single-bound) that can be saved to the graph.
 */

infix fun <FROM : Vertex, TO : Vertex> SingleBoundRelationshipToSingle<FROM, TO>.to(to: TO): Edge<FROM, TO> = BasicEdge(from = from, to = to, relationship = path)

infix fun <FROM : Vertex, TO : Vertex> SingleBoundRelationshipToOptional<FROM, TO>.to(to: TO): Edge<FROM, TO> = BasicEdge(from = from, to = to, relationship = path)
infix fun <FROM : Vertex, TO : Vertex> SingleBoundRelationshipToMany<FROM, TO>.to(to: TO): Edge<FROM, TO> = BasicEdge(from = from, to = to, relationship = path)
infix fun <FROM : Vertex, TO : Vertex> SingleBoundRelationshipToMany<FROM, TO>.to(tos: Iterable<TO>): List<Edge<FROM, TO>> = tos.map { BasicEdge(from = from, to = it, relationship = path) }
infix fun <FROM : Vertex, TO : Vertex> BoundRelationshipToOptional<FROM, TO>.to(to: TO): List<Edge<FROM, TO>> = froms.map { BasicEdge(from = it, to = to, relationship = path) }
infix fun <FROM : Vertex, TO : Vertex> BoundRelationshipToSingle<FROM, TO>.to(to: TO): List<Edge<FROM, TO>> = froms.map { BasicEdge(from = it, to = to, relationship = path) }
