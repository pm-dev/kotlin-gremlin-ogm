package org.apache.tinkerpop.gremlin.ogm.traversals

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

open class GraphTraversalToSingle<TO> internal constructor(
        val traversal: GraphTraversal<*, TO>
) {
    fun traverse(): TO = traversal.toList().single()

    fun asToOptional() = GraphTraversalToOptional(traversal)

    fun asToMany() = GraphTraversalToMany(traversal)
}
