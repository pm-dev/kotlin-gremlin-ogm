package org.apache.tinkerpop.gremlin.ogm.traversals

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

class GraphTraversalToOptional<TO> internal constructor(
        val traversal: GraphTraversal<*, TO>
) {
    fun traverse(): TO? = traversal.toList().singleOrNull()

    fun asToSingle() = GraphTraversalToSingle(traversal)

    fun asToMany() = GraphTraversalToMany(traversal)
}
