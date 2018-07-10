package org.apache.tinkerpop.gremlin.ogm.traversals

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

class GraphTraversalToOptional<FROM, TO> internal constructor(val traversal: GraphTraversal<FROM, TO>) {

    fun fetch(): TO? = traversal.tryNext().orElse(null)

    fun toSingle() = GraphTraversalToSingle(traversal)

    fun toMany() = GraphTraversalToMany(traversal)
}

fun <FROM, TO> GraphTraversal<FROM, TO>.toOptional() = GraphTraversalToOptional(this)
