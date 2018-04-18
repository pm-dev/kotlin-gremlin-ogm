package org.apache.tinkerpop.gremlin.ogm.paths.relationships

/**
 * A Edge represents a FROM and TO vertex that are connected through a relationship.
 * Currently, Edges do not support their own properties, but that is a top priority of the next iteration
 * of this library.
 */
class Edge<FROM : Any, TO : Any>(val from: FROM, val to: TO, val relationship: Relationship<FROM, TO>) {

    /**
     * This edge in the reversed direction.
     */
    val inverse: Edge<TO, FROM> get() = Edge(to, from, relationship.inverse)
}
