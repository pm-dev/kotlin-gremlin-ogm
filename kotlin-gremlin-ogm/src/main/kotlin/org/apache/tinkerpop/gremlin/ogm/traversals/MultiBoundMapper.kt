package org.apache.tinkerpop.gremlin.ogm.traversals

import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.bound.from
import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step

data class MultiBoundMapper<FROM : Vertex> internal constructor(
        val froms: Collection<FROM>,
        val mapper: GraphMapper
) {
    infix fun <TO> traversal(step: Step.ToSingle<FROM, TO>): MultiBoundGraphTraversalToSingle<FROM, TO> = mapper.traversal(step from froms)
    infix fun <TO> traversal(step: Step.ToOptional<FROM, TO>): MultiBoundGraphTraversalToOptional<FROM, TO> = mapper.traversal(step from froms)
    infix fun <TO> traversal(step: Step.ToMany<FROM, TO>): MultiBoundGraphTraversalToMany<FROM, TO> = mapper.traversal(step from froms)

    infix fun <TO> traverse(step: Step.ToOptional<FROM, TO>): Map<FROM, TO?> = traversal(step).traverse()
    infix fun <TO> traverse(step: Step.ToSingle<FROM, TO>): Map<FROM, TO> = traversal(step).traverse()
    infix fun <TO> traverse(step: Step.ToMany<FROM, TO>): Map<FROM, List<TO>> = traversal(step).traverse()
}
