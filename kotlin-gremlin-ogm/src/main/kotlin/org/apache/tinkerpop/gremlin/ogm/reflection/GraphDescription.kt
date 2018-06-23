package org.apache.tinkerpop.gremlin.ogm.reflection

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.extensions.filterNullValues
import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.SerializedProperty
import org.apache.tinkerpop.gremlin.ogm.mappers.scalar.InstantPropertyMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.scalar.UUIDPropertyMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.scalar.identity.*
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import java.time.Instant
import java.util.*
import kotlin.reflect.KClass

internal class GraphDescription (
        vertices: Set<KClass<out Vertex>>,
        relationships: Map<Relationship<out Vertex, out Vertex>, KClass<out Edge<Vertex, Vertex>>?> = mapOf(),
        nestedObjects: Set<KClass<*>> = setOf(),
        private val scalarMappers: Map<KClass<*>, PropertyBiMapper<*, *>> = mapOf()
) {

    val vertexDescriptions: Map<KClass<out Vertex>, VertexDescription<out Vertex>> = vertices.associate { it to VertexDescription(it) }

    val edgeDescriptions: Map<KClass<out Edge<Vertex, Vertex>>, EdgeDescription<Vertex, Vertex, out Edge<Vertex, Vertex>>> =
            relationships.filterNullValues().entries.associate { it.value to EdgeDescription(it.key, it.value) }

    val relationshipsByName: Map<String, Relationship<out Vertex, out Vertex>> = relationships.keys.associateBy { it.name }

    private val nestedObjectDescriptions: Map<KClass<*>, NestedObjectDescription<*>> = nestedObjects.associate { it to NestedObjectDescription(it) }

    private val vertexDescriptionsByLabel: Map<String, VertexDescription<*>> = vertexDescriptions.mapKeys { it.value.label }

    private val edgeDescriptionsByLabel: Map<String, EdgeDescription<*, *, *>> = edgeDescriptions.mapKeys { it.value.label }

    @Suppress("UNCHECKED_CAST")
    fun <T: Vertex> getVertexDescription(deserializedClass: KClass<out T>): VertexDescription<T>? =
            vertexDescriptions[deserializedClass] as? VertexDescription<T>

    @Suppress("UNCHECKED_CAST")
    fun <T: Vertex> getVertexDescription(label: String): VertexDescription<T>? =
            vertexDescriptionsByLabel[label] as? VertexDescription<T>

    @Suppress("UNCHECKED_CAST")
    fun <FROM : Vertex, TO : Vertex, E: Edge<FROM, TO>> getEdgeDescription(deserializedClass: KClass<out E>): EdgeDescription<FROM, TO, E>? =
            edgeDescriptions[deserializedClass] as? EdgeDescription<FROM, TO, E>

    @Suppress("UNCHECKED_CAST")
    fun <FROM : Vertex, TO : Vertex, E: Edge<FROM, TO>> getEdgeDescription(label: String): EdgeDescription<FROM, TO, E>? =
            edgeDescriptionsByLabel[label] as? EdgeDescription<FROM, TO, E>

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getNestedObjectDescription(deserializedClass: KClass<out T>): ObjectDescription<T>? =
            nestedObjectDescriptions[deserializedClass] as? ObjectDescription<T>

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getScalarMapper(deserializedClass: KClass<out T>): PropertyBiMapper<T, SerializedProperty>? =
            scalarMappers[deserializedClass] as? PropertyBiMapper<T, SerializedProperty>

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getDefaultPropertyMapper(deserializedClass: KClass<out T>): PropertyBiMapper<T, SerializedProperty>? =
            defaultPropertyMappers[deserializedClass] as? PropertyBiMapper<T, SerializedProperty>

    companion object {

        private val defaultPropertyMappers = mapOf<KClass<*>, PropertyBiMapper<*, *>>(
                String::class to StringPropertyMapper,
                Byte::class to BytePropertyMapper,
                Float::class to FloatPropertyMapper,
                Double::class to DoublePropertyManager,
                Long::class to LongPropertyMapper,
                Int::class to IntegerPropertyMapper,
                Boolean::class to BooleanPropertyMapper,
                Instant::class to InstantPropertyMapper,
                UUID::class to UUIDPropertyMapper)
    }
}
