package org.apache.tinkerpop.gremlin.ogm.reflection

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.SerializedProperty
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import kotlin.reflect.KClass

/**
 * An interface that describes a graph's vertices, edges, object properties, scalar properties and is
 * used by a GraphMapper to serialize/deserialize these instances to/from the graph, respectively.
 */
interface GraphDescription {

    /**
     *  Vertices
     */

    val vertexClasses: Set<KClass<out Vertex>>

    fun <T : Vertex> getVertexDescription(vertexClass: KClass<out T>): VertexDescription<T>

    val vertexLabels: Set<String>

    fun <T : Vertex> getVertexDescription(vertexLabel: String): VertexDescription<T>

    /**
     * Edges
     */

    val edgeClasses: Set<KClass<out Edge<Vertex, Vertex>>>

    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> getEdgeDescription(edgeClass: KClass<out E>): EdgeDescription<FROM, TO, E>

    val edgeLabels: Set<String>

    fun <FROM : Vertex, TO : Vertex> getEdgeRelationship(edgeLabel: String): Relationship<FROM, TO>

    fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> getEdgeDescription(edgeLabel: String): EdgeDescription<FROM, TO, E>?

    /**
     * Nested Objects
     */

    val objectPropertyClasses: Set<KClass<out Any>>

    fun <T : Any> getObjectPropertyDescription(objectPropertyClass: KClass<out T>): ObjectDescription<T>

    /**
     * Scalars
     */

    val scalarPropertyClasses: Set<KClass<out Any>>

    fun <T : Any> getScalarPropertyMapper(scalarClass: KClass<out T>): PropertyBiMapper<T, SerializedProperty>
}
