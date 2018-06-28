@file:Suppress("unused", "UNUSED_PARAMETER")

package org.janusgraph.ogm.reflection

import org.apache.tinkerpop.gremlin.ogm.annotations.*
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import org.apache.tinkerpop.gremlin.ogm.reflection.CachedGraphDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.assertj.core.api.Assertions.assertThat
import org.janusgraph.core.JanusGraph
import org.janusgraph.ogm.JanusGraphIndicesBuilder
import org.janusgraph.ogm.annotations.Indexed
import org.janusgraph.ogm.exceptions.IndexNotOnProperty
import org.janusgraph.ogm.exceptions.IterableIndexUnsupported
import org.janusgraph.ogm.exceptions.MapIndexUnsupported
import org.janusgraph.ogm.exceptions.NestedIndexUnsupported
import org.junit.Before
import org.junit.Test
import util.example.exampleGraph
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

internal class IndexDescriptionTest {

    private lateinit var g: JanusGraph

    @Before
    fun setup() {
        g = exampleGraph()
    }

    @Test
    fun `test index string vertex property`() {
        @Element("test") class Vert(@ID val id: String?, @Indexed @Property("a") val a: String)
        verify(
                graphDescription = CachedGraphDescription(vertices = setOf(Vert::class)),
                expectedIndicies = 1)
    }

    @Test
    fun `test index boolean vertex property`() {
        @Element("test") class Vert(@ID val id: String?, @Indexed @Property("a") val a: Boolean)
        verify(
                graphDescription = CachedGraphDescription(vertices = setOf(Vert::class)),
                expectedIndicies = 1)
    }

    @Test
    fun `test index byte vertex property`() {
        @Element("test") class Vert(@ID val id: String?, @Indexed @Property("a") val a: Byte)
        verify(
                graphDescription = CachedGraphDescription(vertices = setOf(Vert::class)),
                expectedIndicies = 1)
    }

    @Test
    fun `test index double vertex property`() {
        @Element("test") class Vert(@ID val id: String?, @Indexed @Property("a") val a: Double)
        verify(
                graphDescription = CachedGraphDescription(vertices = setOf(Vert::class)),
                expectedIndicies = 1)
    }

    @Test
    fun `test index float vertex property`() {
        @Element("test") class Vert(@ID val id: String?, @Indexed @Property("a") val a: Float)
        verify(
                graphDescription = CachedGraphDescription(vertices = setOf(Vert::class)),
                expectedIndicies = 1)
    }

    @Test
    fun `test index integer vertex property`() {
        @Element("test") class Vert(@ID val id: String?, @Indexed @Property("a") val a: Int)
        verify(
                graphDescription = CachedGraphDescription(vertices = setOf(Vert::class)),
                expectedIndicies = 1)
    }

    @Test
    fun `test index long vertex property`() {
        @Element("test") class Vert(@ID val id: String?, @Indexed @Property("a") val a: Long)
        verify(
                graphDescription = CachedGraphDescription(vertices = setOf(Vert::class)),
                expectedIndicies = 1)
    }

    @Test
    fun `test index instant vertex property`() {
        @Element("test") class Vert(@ID val id: String?, @Indexed @Property("a") val a: Instant)
        verify(
                graphDescription = CachedGraphDescription(vertices = setOf(Vert::class)),
                expectedIndicies = 1)
    }

    @Test
    fun `test index uuid vertex property`() {
        @Element("test") class Vert(@ID val id: String?, @Indexed @Property("a") val a: UUID)
        verify(
                graphDescription = CachedGraphDescription(vertices = setOf(Vert::class)),
                expectedIndicies = 1)
    }

    @Test
    fun `test index custom type vertex property`() {
        val zonedDateTimeToStringMapper = object : PropertyBiMapper<ZonedDateTime, String> {
            override fun forwardMap(from: ZonedDateTime) = "${from.toInstant().toEpochMilli()}:${from.zone.id}"
            override fun inverseMap(from: String): ZonedDateTime {
                val parts = from.split(':')
                val epochMili = parts[0].toLong()
                val zoneId = parts[1]
                return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMili), ZoneId.of(zoneId))
            }
            override val serializedClass get() = String::class
        }
        @Element("test") class Vert(@ID val id: String?, @Indexed @Property("a") val a: ZonedDateTime)
        verify(
                graphDescription = CachedGraphDescription(
                        vertices = setOf(Vert::class),
                        scalarProperties = mapOf(ZonedDateTime::class to zonedDateTimeToStringMapper)),
                expectedIndicies = 1)
    }

    @Test
    fun `test index nested vertex property`() {
        data class Nested(@Property(key = "a") val a: Int, @Property(key = "b") val b: Int)
        @Element("test") class Vert(@ID val id: String?, @Indexed @Property("a") val a: Nested)
        verify(
                graphDescription = CachedGraphDescription(vertices = setOf(Vert::class), objectProperties = setOf(Nested::class)),
                expectedIndicies = 2)
    }

    @Test
    fun `test index edge properties`() {
        @Element("test")
        class Vert(
                @ID val id: String?,
                @FromVertex override val from: Any,
                @ToVertex override val to: Any,
                @Indexed @Property("a") val a: String) : Edge<Any, Any>
        verify(
                graphDescription = CachedGraphDescription(vertices = setOf(Vert::class)),
                expectedIndicies = 1)
    }

    @Test(expected = MapIndexUnsupported::class)
    fun `test index map property`() {
        @Element("test") class Vert(@ID val id: String?, @Indexed @Property("a") val a: Map<String, String>)
        verify(graphDescription = CachedGraphDescription(vertices = setOf(Vert::class)))
    }

    @Test(expected = IterableIndexUnsupported::class)
    fun `test index collection property`() {
        @Element("test") class Vert(@ID val id: String?, @Indexed @Property("a") val a: List<String>)
       verify(graphDescription = CachedGraphDescription(vertices = setOf(Vert::class)))
    }

    @Test(expected = NestedIndexUnsupported::class)
    fun `test indexed nested object property`() {
        data class Nested(@Property(key = "a") val a: Int, @Indexed @Property(key = "b") val b: Int)
        @Element("test") class Vert(@ID val id: String?, @Indexed @Property("a") val a: Nested)
        verify(graphDescription = CachedGraphDescription(vertices = setOf(Vert::class), objectProperties = setOf(Nested::class)))
    }

    @Test(expected = IndexNotOnProperty::class)
    fun `test index not on property`() {
        @Element("test") class Vert(@Indexed @ID val id: String?, @Property("a") val a: String)
        verify(graphDescription = CachedGraphDescription(vertices = setOf(Vert::class)))
    }

    private fun verify(graphDescription: GraphDescription, expectedIndicies: Int? = null) {
        val indexBuilder = object : JanusGraphIndicesBuilder {
            override val graphDescription: GraphDescription
                get() = graphDescription
        }
        val indices = indexBuilder(g)
        assertThat(expectedIndicies).isNotNull()
        assertThat(indices).hasSize(expectedIndicies!!)
    }
}
