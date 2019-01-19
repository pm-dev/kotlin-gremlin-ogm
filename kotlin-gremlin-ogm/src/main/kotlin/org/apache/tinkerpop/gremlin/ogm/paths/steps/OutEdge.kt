package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.bound.*
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.edgespec.EdgeSpec

infix fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> FROM.outE(edgeSpec: EdgeSpec.ToSingle<FROM, TO>): SingleBoundStep.ToSingle<FROM, E> = SingleBoundStepToSingle<FROM, E>(from = this, step = EdgeStep.ToSingle(edgeSpec))
infix fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> Collection<FROM>.outE(edgeSpec: EdgeSpec.ToSingle<FROM, TO>): BoundStep.ToSingle<FROM, E> = BoundStepToSingle<FROM, E>(froms = this.toList(), step = EdgeStep.ToSingle(edgeSpec))

infix fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> FROM.outE(edgeSpec: EdgeSpec.ToOptional<FROM, TO>): SingleBoundStep.ToOptional<FROM, E> = SingleBoundStepToOptional<FROM, E>(from = this, step = EdgeStep.ToOptional(edgeSpec))
infix fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> Collection<FROM>.outE(edgeSpec: EdgeSpec.ToOptional<FROM, TO>): BoundStep.ToOptional<FROM, E> = BoundStepToOptional<FROM, E>(froms = this.toList(), step = EdgeStep.ToOptional(edgeSpec))

infix fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> FROM.outE(edgeSpec: EdgeSpec.ToMany<FROM, TO>): SingleBoundStep.ToMany<FROM, E> = SingleBoundStepToMany<FROM, E>(from = this, step = EdgeStep.ToMany(edgeSpec))
infix fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> Collection<FROM>.outE(edgeSpec: EdgeSpec.ToMany<FROM, TO>): BoundStep.ToMany<FROM, E> = BoundStepToMany<FROM, E>(froms = this.toList(), step = EdgeStep.ToMany(edgeSpec))

infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Step.ToSingle<FROM, TO>.outE(edgeSpec: EdgeSpec.ToSingle<TO, NEXT>): Step.ToSingle<FROM, E> = to(EdgeStep.ToSingle<TO, NEXT, E>(edgeSpec))
infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Step.ToSingle<FROM, TO>.outE(edgeSpec: EdgeSpec.ToOptional<TO, NEXT>): Step.ToOptional<FROM, E> = to(EdgeStep.ToOptional<TO, NEXT, E>(edgeSpec))
infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Step.ToSingle<FROM, TO>.outE(edgeSpec: EdgeSpec.ToMany<TO, NEXT>): Step.ToMany<FROM, E> = to(EdgeStep.ToMany<TO, NEXT, E>(edgeSpec))

infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Step.ToOptional<FROM, TO>.outE(edgeSpec: EdgeSpec.ToSingle<TO, NEXT>): Step.ToOptional<FROM, E> = to(EdgeStep.ToSingle<TO, NEXT, E>(edgeSpec))
infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Step.ToOptional<FROM, TO>.outE(edgeSpec: EdgeSpec.ToOptional<TO, NEXT>): Step.ToOptional<FROM, E> = to(EdgeStep.ToOptional<TO, NEXT, E>(edgeSpec))
infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Step.ToOptional<FROM, TO>.outE(edgeSpec: EdgeSpec.ToMany<TO, NEXT>): Step.ToMany<FROM, E> = to(EdgeStep.ToMany<TO, NEXT, E>(edgeSpec))

infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Step.ToMany<FROM, TO>.outE(edgeSpec: EdgeSpec.ToSingle<TO, NEXT>): Step.ToMany<FROM, E> = to(EdgeStep.ToSingle<TO, NEXT, E>(edgeSpec))
infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Step.ToMany<FROM, TO>.outE(edgeSpec: EdgeSpec.ToOptional<TO, NEXT>): Step.ToMany<FROM, E> = to(EdgeStep.ToOptional<TO, NEXT, E>(edgeSpec))
infix fun <FROM, TO : Vertex, NEXT : Vertex, E : Edge<TO, NEXT>> Step.ToMany<FROM, TO>.outE(edgeSpec: EdgeSpec.ToMany<TO, NEXT>): Step.ToMany<FROM, E> = to(EdgeStep.ToMany<TO, NEXT, E>(edgeSpec))
