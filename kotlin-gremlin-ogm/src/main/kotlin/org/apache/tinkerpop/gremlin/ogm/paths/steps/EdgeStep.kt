package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.bound.*
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

interface EdgeStep<FROM : Vertex, out TO : Vertex, E : Edge<FROM, TO>> : Step<FROM, E> {

    open class ToSingle<FROM : Vertex, out TO : Vertex, E : Edge<FROM, TO>>(
            private val relationship: Relationship.ToSingle<FROM, TO>
    ) : Step.ToSingle<FROM, E>({ traverser ->

        traverser.traversal.map {
            traverser.graphMapper.serialize(it.get())
        }.outE(relationship.name).map {
            traverser.graphMapper.deserialize<FROM, TO, E>(it.get())
        }
    })

    open class ToOptional<FROM : Vertex, out TO : Vertex, E : Edge<FROM, TO>>(
            private val relationship: Relationship.ToOptional<FROM, TO>
    ) : Step.ToOptional<FROM, E>({ traverser ->

        traverser.traversal.map {
            traverser.graphMapper.serialize(it.get())
        }.outE(relationship.name).map {
            traverser.graphMapper.deserialize<FROM, TO, E>(it.get())
        }
    })

    open class ToMany<FROM : Vertex, out TO : Vertex, E : Edge<FROM, TO>>(
            private val relationship: Relationship.ToMany<FROM, TO>
    ) : Step.ToMany<FROM, E>({ traverser ->

        traverser.traversal.map {
            traverser.graphMapper.serialize(it.get())
        }.outE(relationship.name).map {
            traverser.graphMapper.deserialize<FROM, TO, E>(it.get())
        }
    })
}

infix fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> FROM.outE(relationship: Relationship.ToSingle<FROM, TO>) = SingleBoundPathToSingle<FROM, E>(from = this, path = EdgeStep.ToSingle(relationship))
infix fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> Iterable<FROM>.outE(relationship: Relationship.ToSingle<FROM, TO>) = BoundPathToSingle<FROM, E>(froms = this, path = EdgeStep.ToSingle(relationship))

infix fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> FROM.outE(relationship: Relationship.ToOptional<FROM, TO>) = SingleBoundPathToOptional<FROM, E>(from = this, path = EdgeStep.ToOptional(relationship))
infix fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> Iterable<FROM>.outE(relationship: Relationship.ToOptional<FROM, TO>) = BoundPathToOptional<FROM, E>(froms = this, path = EdgeStep.ToOptional(relationship))

infix fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> FROM.outE(relationship: Relationship.ToMany<FROM, TO>) = SingleBoundPathToMany<FROM, E>(from = this, path = EdgeStep.ToMany(relationship))
infix fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> Iterable<FROM>.outE(relationship: Relationship.ToMany<FROM, TO>) = BoundPathToMany<FROM, E>(froms = this, path = EdgeStep.ToMany(relationship))

infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Path.ToSingle<FROM, TO>.outE(relationship: Relationship.ToSingle<TO, NEXT>): Path.ToSingle<FROM, E> = to(EdgeStep.ToSingle<TO, NEXT, E>(relationship))
infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Path.ToSingle<FROM, TO>.outE(relationship: Relationship.ToOptional<TO, NEXT>): Path.ToOptional<FROM, E> = to(EdgeStep.ToOptional<TO, NEXT, E>(relationship))
infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Path.ToSingle<FROM, TO>.outE(relationship: Relationship.ToMany<TO, NEXT>): Path.ToMany<FROM, E> = to(EdgeStep.ToMany<TO, NEXT, E>(relationship))

infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Path.ToOptional<FROM, TO>.outE(relationship: Relationship.ToSingle<TO, NEXT>): Path.ToOptional<FROM, E> = to(EdgeStep.ToSingle<TO, NEXT, E>(relationship))
infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Path.ToOptional<FROM, TO>.outE(relationship: Relationship.ToOptional<TO, NEXT>): Path.ToOptional<FROM, E> = to(EdgeStep.ToOptional<TO, NEXT, E>(relationship))
infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Path.ToOptional<FROM, TO>.outE(relationship: Relationship.ToMany<TO, NEXT>): Path.ToMany<FROM, E> = to(EdgeStep.ToMany<TO, NEXT, E>(relationship))

infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Path.ToMany<FROM, TO>.outE(relationship: Relationship.ToSingle<TO, NEXT>): Path.ToMany<FROM, E> = to(EdgeStep.ToSingle<TO, NEXT, E>(relationship))
infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Path.ToMany<FROM, TO>.outE(relationship: Relationship.ToOptional<TO, NEXT>): Path.ToMany<FROM, E> = to(EdgeStep.ToOptional<TO, NEXT, E>(relationship))
infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Path.ToMany<FROM, TO>.outE(relationship: Relationship.ToMany<TO, NEXT>): Path.ToMany<FROM, E> = to(EdgeStep.ToMany<TO, NEXT, E>(relationship))
