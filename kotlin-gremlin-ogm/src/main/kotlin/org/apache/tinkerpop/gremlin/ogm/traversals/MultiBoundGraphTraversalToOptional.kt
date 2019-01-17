package org.apache.tinkerpop.gremlin.ogm.traversals

import org.apache.tinkerpop.gremlin.ogm.extensions.toOptionalMap
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

class MultiBoundGraphTraversalToOptional<FROM, TO> internal constructor(
        val froms: List<FROM>,
        val traversal: GraphTraversal<*, Pair<FROM, TO>>
) : Iterable<Pair<FROM, TO>> {

    override fun iterator() = traversal

    fun traverse(): Map<FROM, TO?> = toOptionalMap(froms)

    fun asToSingle() = MultiBoundGraphTraversalToSingle(froms, traversal)

    fun asToMany() = MultiBoundGraphTraversalToMany(froms, traversal)
}
