package org.apache.tinkerpop.gremlin.ogm.traversals

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

class SingleBoundGraphTraversalToMany<TO> internal constructor(
        val traversal: GraphTraversal<*, TO>
) : Iterable<TO> {

    override fun iterator() = traversal

    fun traverse(): List<TO> = traversal.toList()

    fun asToSingle() = SingleBoundGraphTraversalToSingle(traversal)

    fun asToOptional() = SingleBoundGraphTraversalToOptional(traversal)
}
