package org.apache.tinkerpop.gremlin.ogm.reflection

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.*
import org.apache.tinkerpop.gremlin.ogm.extensions.filterNullValues
import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.SerializedProperty
import org.apache.tinkerpop.gremlin.ogm.mappers.scalar.BigDecimalPropertyMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.scalar.InstantPropertyMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.scalar.URLPropertyMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.scalar.UUIDPropertyMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.scalar.identity.*
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import java.math.BigDecimal
import java.net.URL
import java.time.Instant
import java.util.*
import kotlin.reflect.KClass

open class CachedGraphDescription(
        vertices: Set<KClass<out Vertex>>,
        relationships: Map<Relationship<out Vertex, out Vertex>, KClass<out Edge<Vertex, Vertex>>?> = mapOf(),
        objectProperties: Set<KClass<*>> = setOf(),
        scalarProperties: Map<KClass<*>, PropertyBiMapper<*, *>> = emptyMap()
) : GraphDescription {

    private val scalarMappers = builtinScalarPropertyMappers + scalarProperties

    private val vertexDescriptionsByClass = vertices
            .associate { vertexClass ->
                vertexClass to VertexDescription(vertexClass)
            }

    private val vertexDescriptionsByLabel = vertexDescriptionsByClass
            .mapKeys {
                it.value.label
            }

    private val edgeDescriptionsByClass = relationships
            .filterNullValues()
            .entries
            .associate { (relationship, edgeClass) ->
                edgeClass to EdgeDescription(relationship, edgeClass)
            }

    private val edgeDescriptionsByLabel = edgeDescriptionsByClass
            .mapKeys {
                it.value.label
            }

    private val objectPropertyDescriptionsByClass = objectProperties
            .associate { objectPropertyClass ->
                objectPropertyClass to ObjectPropertyDescription(objectPropertyClass)
            }

    private val relationshipsByLabel = relationships.keys
            .groupBy(Relationship<out Vertex, out Vertex>::name)
            .mapValues { (name, relationships) ->
                if (relationships.size > 1) throw DuplicateRelationshipName(name = name)
                relationships.single()
            }

    override val vertexClasses get() = vertexDescriptionsByClass.keys

    override val vertexLabels get() = vertexDescriptionsByLabel.keys

    override val edgeClasses get() = edgeDescriptionsByClass.keys

    override val edgeLabels get() = relationshipsByLabel.keys

    override val objectPropertyClasses get() = objectPropertyDescriptionsByClass.keys

    override val scalarPropertyClasses get() = scalarMappers.keys

    @Suppress("UNCHECKED_CAST")
    override fun <T : Vertex> getVertexDescription(vertexClass: KClass<out T>): VertexDescription<T> =
            vertexDescriptionsByClass[vertexClass] as? VertexDescription<T> ?: throw UnregisteredClass(vertexClass)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Vertex> getVertexDescription(vertexLabel: String): VertexDescription<T> =
            vertexDescriptionsByLabel[vertexLabel] as? VertexDescription<T> ?: throw UnregisteredLabel(vertexLabel)

    @Suppress("UNCHECKED_CAST")
    override fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> getEdgeDescription(edgeClass: KClass<out E>): EdgeDescription<FROM, TO, E> =
            edgeDescriptionsByClass[edgeClass] as? EdgeDescription<FROM, TO, E> ?: throw UnregisteredClass(edgeClass)

    @Suppress("UNCHECKED_CAST")
    override fun <FROM : Vertex, TO : Vertex> getEdgeRelationship(edgeLabel: String): Relationship<FROM, TO> =
            relationshipsByLabel[edgeLabel] as? Relationship<FROM, TO> ?: throw UnregisteredLabel(edgeLabel)

    @Suppress("UNCHECKED_CAST")
    override fun <FROM : Vertex, TO : Vertex, E : Edge<FROM, TO>> getEdgeDescription(edgeLabel: String): EdgeDescription<FROM, TO, E>? =
            edgeDescriptionsByLabel[edgeLabel] as? EdgeDescription<FROM, TO, E>

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getObjectPropertyDescription(objectPropertyClass: KClass<out T>): ObjectDescription<T> =
            objectPropertyDescriptionsByClass[objectPropertyClass] as? ObjectDescription<T>
                    ?: throw ObjectDescriptionMissing(objectPropertyClass)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getScalarPropertyMapper(scalarClass: KClass<out T>): PropertyBiMapper<T, SerializedProperty> =
            scalarMappers[scalarClass] as? PropertyBiMapper<T, SerializedProperty>
                    ?: throw PropertyMapperMissing(scalarClass)

    companion object {

        internal val builtinScalarPropertyMappers = mapOf<KClass<*>, PropertyBiMapper<*, *>>(
                String::class to StringPropertyMapper,
                Byte::class to BytePropertyMapper,
                Float::class to FloatPropertyMapper,
                Double::class to DoublePropertyManager,
                Long::class to LongPropertyMapper,
                Int::class to IntegerPropertyMapper,
                Boolean::class to BooleanPropertyMapper,
                Instant::class to InstantPropertyMapper,
                UUID::class to UUIDPropertyMapper,
                URL::class to URLPropertyMapper,
                BigDecimal::class to BigDecimalPropertyMapper)
    }
}
