package org.apache.tinkerpop.gremlin.ogm.traversals

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

class GraphTraversalToSingle<FROM, TO> internal constructor(val traversal: GraphTraversal<FROM, TO>) {

    fun fetch(): TO = traversal.next()

    fun toOptional() = GraphTraversalToOptional(traversal)

    fun toMany() = GraphTraversalToMany(traversal)
}

fun <FROM, TO> GraphTraversal<FROM, TO>.toSingle() = GraphTraversalToSingle(this)
