package org.apache.tinkerpop.gremlin.ogm.traversals

import org.apache.tinkerpop.gremlin.ogm.extensions.toSingleMap
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

class MultiBoundGraphTraversalToSingle<FROM, TO> internal constructor(
        val froms: List<FROM>,
        val traversal: GraphTraversal<*, Pair<FROM, TO>>
) : Iterable<Pair<FROM, TO>> {

    override fun iterator() = traversal

    fun traverse(): Map<FROM, TO> = toSingleMap(froms)

    fun asToMany() = MultiBoundGraphTraversalToMany(froms, traversal)

    fun asToOptional() = MultiBoundGraphTraversalToOptional(froms, traversal)
}


