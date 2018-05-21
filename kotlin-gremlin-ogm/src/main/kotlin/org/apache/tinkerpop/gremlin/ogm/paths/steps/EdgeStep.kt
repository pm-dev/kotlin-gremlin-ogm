package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.bound.*
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.BaseEdge
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

interface EdgeStep<OUT : Any, IN : Any, E: BaseEdge<OUT, IN>> : Step<OUT, E> {

    open class ToSingle<OUT : Any, IN : Any, E: BaseEdge<OUT, IN>>(
            private val relationship: Relationship.ToSingle<OUT, IN>
    ) : Step.ToSingle<OUT, E>({ traverser ->

        traverser.traversal.map {
            traverser.vertexMapper.forwardMap(it.get())
        }.outE(relationship.name).map {
            @Suppress("UNCHECKED_CAST")
            traverser.edgeMapper.inverseMap(it.get()) as E
        }
    })

    open class ToOptional<OUT : Any, IN : Any, E: BaseEdge<OUT, IN>>(
            private val relationship: Relationship.ToOptional<OUT, IN>
    ) : Step.ToOptional<OUT, E>({ traverser ->

        traverser.traversal.map {
            traverser.vertexMapper.forwardMap(it.get())
        }.outE(relationship.name).map {
            @Suppress("UNCHECKED_CAST")
            traverser.edgeMapper.inverseMap(it.get()) as E
        }
    })

    open class ToMany<OUT : Any, IN : Any, E: BaseEdge<OUT, IN>>(
            private val relationship: Relationship.ToMany<OUT, IN>
    ) : Step.ToMany<OUT, E>({ traverser ->

        traverser.traversal.map {
            traverser.vertexMapper.forwardMap(it.get())
        }.outE(relationship.name).map {
            @Suppress("UNCHECKED_CAST")
            traverser.edgeMapper.inverseMap(it.get()) as E
        }
    })
}

infix fun <OUT : Any, IN : Any, E : BaseEdge<OUT, IN>> OUT.outE(relationship: Relationship.ToSingle<OUT, IN>) = SingleBoundPathToSingle<OUT, E>(outV = this, path = EdgeStep.ToSingle(relationship))
infix fun <OUT : Any, IN : Any, E : BaseEdge<OUT, IN>> Iterable<OUT>.outE(relationship: Relationship.ToSingle<OUT, IN>) = BoundPathToSingle<OUT, E>(outVs = this, path = EdgeStep.ToSingle(relationship))

infix fun <OUT : Any, IN : Any, E : BaseEdge<OUT, IN>> OUT.outE(relationship: Relationship.ToOptional<OUT, IN>) = SingleBoundPathToOptional<OUT, E>(outV = this, path = EdgeStep.ToOptional(relationship))
infix fun <OUT : Any, IN : Any, E : BaseEdge<OUT, IN>> Iterable<OUT>.outE(relationship: Relationship.ToOptional<OUT, IN>) = BoundPathToOptional<OUT, E>(outVs = this, path = EdgeStep.ToOptional(relationship))

infix fun <OUT : Any, IN : Any, E : BaseEdge<OUT, IN>> OUT.outE(relationship: Relationship.ToMany<OUT, IN>) = SingleBoundPathToMany<OUT, E>(outV = this, path = EdgeStep.ToMany(relationship))
infix fun <OUT : Any, IN : Any, E : BaseEdge<OUT, IN>> Iterable<OUT>.outE(relationship: Relationship.ToMany<OUT, IN>) = BoundPathToMany<OUT, E>(outVs = this, path = EdgeStep.ToMany(relationship))

infix fun <OUT, IN : Any, NEXT : Any, E : BaseEdge<IN, NEXT>> Path.ToSingle<OUT, IN>.outE(relationship: Relationship.ToSingle<IN, NEXT>): Path.ToSingle<OUT, E> = to(EdgeStep.ToSingle<IN, NEXT, E>(relationship))
infix fun <OUT, IN : Any, NEXT : Any, E : BaseEdge<IN, NEXT>> Path.ToSingle<OUT, IN>.outE(relationship: Relationship.ToOptional<IN, NEXT>): Path.ToOptional<OUT, E> = to(EdgeStep.ToOptional<IN, NEXT, E>(relationship))
infix fun <OUT, IN : Any, NEXT : Any, E : BaseEdge<IN, NEXT>> Path.ToSingle<OUT, IN>.outE(relationship: Relationship.ToMany<IN, NEXT>): Path.ToMany<OUT, E> = to(EdgeStep.ToMany<IN, NEXT, E>(relationship))

infix fun <OUT, IN : Any, NEXT : Any, E : BaseEdge<IN, NEXT>> Path.ToOptional<OUT, IN>.outE(relationship: Relationship.ToSingle<IN, NEXT>): Path.ToOptional<OUT, E> = to(EdgeStep.ToSingle<IN, NEXT, E>(relationship))
infix fun <OUT, IN : Any, NEXT : Any, E : BaseEdge<IN, NEXT>> Path.ToOptional<OUT, IN>.outE(relationship: Relationship.ToOptional<IN, NEXT>): Path.ToOptional<OUT, E> = to(EdgeStep.ToOptional<IN, NEXT, E>(relationship))
infix fun <OUT, IN : Any, NEXT : Any, E : BaseEdge<IN, NEXT>> Path.ToOptional<OUT, IN>.outE(relationship: Relationship.ToMany<IN, NEXT>): Path.ToMany<OUT, E> = to(EdgeStep.ToMany<IN, NEXT, E>(relationship))

infix fun <OUT, IN : Any, NEXT : Any, E : BaseEdge<IN, NEXT>> Path.ToMany<OUT, IN>.outE(relationship: Relationship.ToSingle<IN, NEXT>): Path.ToMany<OUT, E> = to(EdgeStep.ToSingle<IN, NEXT, E>(relationship))
infix fun <OUT, IN : Any, NEXT : Any, E : BaseEdge<IN, NEXT>> Path.ToMany<OUT, IN>.outE(relationship: Relationship.ToOptional<IN, NEXT>): Path.ToMany<OUT, E> = to(EdgeStep.ToOptional<IN, NEXT, E>(relationship))
infix fun <OUT, IN : Any, NEXT : Any, E : BaseEdge<IN, NEXT>> Path.ToMany<OUT, IN>.outE(relationship: Relationship.ToMany<IN, NEXT>): Path.ToMany<OUT, E> = to(EdgeStep.ToMany<IN, NEXT, E>(relationship))
