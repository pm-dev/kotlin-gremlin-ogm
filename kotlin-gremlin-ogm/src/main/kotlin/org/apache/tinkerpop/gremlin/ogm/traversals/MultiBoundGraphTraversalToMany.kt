package org.apache.tinkerpop.gremlin.ogm.traversals

import org.apache.tinkerpop.gremlin.ogm.extensions.toMultiMap
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

class MultiBoundGraphTraversalToMany<FROM, TO> internal constructor(
        val froms: List<FROM>,
        val traversal: GraphTraversal<*, Pair<FROM, TO>>
) : Iterable<Pair<FROM, TO>> {

    override fun iterator() = traversal

    fun traverse(): Map<FROM, List<TO>> = toMultiMap(froms)

    fun asToSingle() = MultiBoundGraphTraversalToSingle(froms, traversal)

    fun asToOptional() = MultiBoundGraphTraversalToOptional(froms, traversal)
}
