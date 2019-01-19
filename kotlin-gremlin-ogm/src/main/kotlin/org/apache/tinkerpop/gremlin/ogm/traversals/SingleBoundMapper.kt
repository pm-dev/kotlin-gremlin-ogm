package org.apache.tinkerpop.gremlin.ogm.traversals

import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.bound.from
import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step

data class SingleBoundMapper<FROM : Vertex> internal constructor(
        val from: FROM,
        val mapper: GraphMapper
) {
    infix fun <TO> traversal(step: Step.ToSingle<FROM, TO>): SingleBoundGraphTraversalToSingle<TO> = mapper.traversal(step from from)
    infix fun <TO> traversal(step: Step.ToOptional<FROM, TO>): SingleBoundGraphTraversalToOptional<TO> = mapper.traversal(step from from)
    infix fun <TO> traversal(step: Step.ToMany<FROM, TO>): SingleBoundGraphTraversalToMany<TO> = mapper.traversal(step from from)

    infix fun <TO> traverse(step: Step.ToMany<FROM, TO>): List<TO> = traversal(step).traverse()
    infix fun <TO> traverse(step: Step.ToOptional<FROM, TO>): TO? = traversal(step).traverse()
    infix fun <TO> traverse(step: Step.ToSingle<FROM, TO>): TO = traversal(step).traverse()
}
