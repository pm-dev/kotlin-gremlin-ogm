package org.apache.tinkerpop.gremlin.ogm.traversals

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

class GraphTraversalToMany<FROM, TO> internal constructor(val traversal: GraphTraversal<FROM, TO>) : Sequence<TO> {

    override fun iterator() = traversal

    fun fetch(): List<TO> = toList()

    fun toSingle() = GraphTraversalToSingle(traversal)

    fun toOptional() = GraphTraversalToOptional(traversal)
}

fun <FROM, TO> GraphTraversal<FROM, TO>.toMany() = GraphTraversalToMany(this)
