package org.apache.tinkerpop.gremlin.ogm.traversals

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

class SingleBoundGraphTraversalToOptional<TO> internal constructor(
        val traversal: GraphTraversal<*, TO>
) {
    fun traverse(): TO? = traversal.toList().singleOrNull()

    fun asToSingle() = SingleBoundGraphTraversalToSingle(traversal)

    fun asToMany() = SingleBoundGraphTraversalToMany(traversal)
}
