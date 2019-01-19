@file:Suppress("unused")

package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.edgespec.EdgeSpec
import org.apache.tinkerpop.gremlin.ogm.traversals.MultiBoundMapper
import org.apache.tinkerpop.gremlin.ogm.traversals.SingleBoundMapper


infix fun <FROM : Vertex, TO> Step.ToMany<FROM, TO>.from(froms: Collection<FROM>): BoundStep.ToMany<FROM, TO> = BoundStepToMany(froms.toList(), this)

infix fun <FROM : Vertex, TO> Step.ToMany<FROM, TO>.from(from: FROM): SingleBoundStep.ToMany<FROM, TO> = SingleBoundStepToMany(from, this)

fun <FROM : Vertex, TO> Step.ToMany<FROM, TO>.from(vararg froms: FROM): BoundStep.ToMany<FROM, TO> = BoundStepToMany(froms.toList(), this)

infix fun <FROM : Vertex, TO> Step.ToOptional<FROM, TO>.from(from: FROM): SingleBoundStep.ToOptional<FROM, TO> = SingleBoundStepToOptional(from, this)

infix fun <FROM : Vertex, TO> Step.ToOptional<FROM, TO>.from(froms: Collection<FROM>): BoundStep.ToOptional<FROM, TO> = BoundStepToOptional(froms.toList(), this)

fun <FROM : Vertex, TO> Step.ToOptional<FROM, TO>.from(vararg froms: FROM): BoundStep.ToOptional<FROM, TO> = BoundStepToOptional(froms.toList(), this)

infix fun <FROM : Vertex, TO> Step.ToSingle<FROM, TO>.from(from: FROM): SingleBoundStep.ToSingle<FROM, TO> = SingleBoundStepToSingle(from, this)
infix fun <FROM : Vertex, TO> Step.ToSingle<FROM, TO>.from(froms: Collection<FROM>): BoundStep.ToSingle<FROM, TO> = BoundStepToSingle(froms.toList(), this)
fun <FROM : Vertex, TO> Step.ToSingle<FROM, TO>.from(vararg froms: FROM): BoundStep.ToSingle<FROM, TO> = BoundStepToSingle(froms.toList(), this)

infix fun <FROM : Vertex, TO : Vertex> EdgeSpec.ToMany<FROM, TO>.from(from: FROM): SingleBoundEdgeSpec.ToMany<FROM, TO> = SingleBoundEdgeSpecToMany(from, this)
infix fun <FROM : Vertex, TO : Vertex> EdgeSpec.ToMany<FROM, TO>.from(froms: Collection<FROM>): BoundEdgeSpec.ToMany<FROM, TO> = BoundEdgeSpecToMany(froms.toList(), this)
fun <FROM : Vertex, TO : Vertex> EdgeSpec.ToMany<FROM, TO>.from(vararg froms: FROM): BoundEdgeSpec.ToMany<FROM, TO> = BoundEdgeSpecToMany(froms.toList(), this)

infix fun <FROM : Vertex, TO : Vertex> EdgeSpec.ToOptional<FROM, TO>.from(from: FROM): SingleBoundEdgeSpec.ToOptional<FROM, TO> = SingleBoundEdgeSpecToOptional(from, this)
infix fun <FROM : Vertex, TO : Vertex> EdgeSpec.ToOptional<FROM, TO>.from(froms: Collection<FROM>): BoundEdgeSpec.ToOptional<FROM, TO> = BoundEdgeSpecToOptional(froms.toList(), this)
fun <FROM : Vertex, TO : Vertex> EdgeSpec.ToOptional<FROM, TO>.from(vararg froms: FROM): BoundEdgeSpec.ToOptional<FROM, TO> = BoundEdgeSpecToOptional(froms.toList(), this)

infix fun <FROM : Vertex, TO : Vertex> EdgeSpec.ToSingle<FROM, TO>.from(from: FROM): SingleBoundEdgeSpec.ToSingle<FROM, TO> = SingleBoundEdgeSpecToSingle(from, this)
infix fun <FROM : Vertex, TO : Vertex> EdgeSpec.ToSingle<FROM, TO>.from(froms: Collection<FROM>): BoundEdgeSpec.ToSingle<FROM, TO> = BoundEdgeSpecToSingle(froms.toList(), this)
fun <FROM : Vertex, TO : Vertex> EdgeSpec.ToSingle<FROM, TO>.from(vararg froms: FROM): BoundEdgeSpec.ToSingle<FROM, TO> = BoundEdgeSpecToSingle(froms.toList(), this)

infix fun <FROM : Vertex> GraphMapper.bind(vertex: FROM) = SingleBoundMapper(vertex, this)
infix fun <FROM : Vertex> GraphMapper.bind(vertices: Collection<FROM>) = MultiBoundMapper(vertices, this)
fun <FROM : Vertex> GraphMapper.bind(vararg vertices: FROM) = MultiBoundMapper(vertices.toList(), this)

