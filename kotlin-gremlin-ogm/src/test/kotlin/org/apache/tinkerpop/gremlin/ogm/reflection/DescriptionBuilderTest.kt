@file:Suppress("unused", "UNUSED_PARAMETER")

package org.apache.tinkerpop.gremlin.ogm.reflection

import org.apache.tinkerpop.gremlin.ogm.GraphMapper.Companion.idTag
import org.apache.tinkerpop.gremlin.ogm.annotations.*
import org.apache.tinkerpop.gremlin.ogm.exceptions.*
import org.apache.tinkerpop.gremlin.ogm.extensions.nestedPropertyDelimiter
import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.Test
import util.example.Base64Mapper

internal class DescriptionBuilderTest {

    class LongToStringMapper : PropertyBiMapper<Long, String> {
        override fun forwardMap(from: Long) = from.toString()
        override fun inverseMap(from: String) = from.toLong()
    }

    @Vertex("test") interface VertInterface

    @Test(expected = PrimaryConstructorMissing::class)
    fun `test no primary constructor`() {
        VertexDescription(VertInterface::class)
    }

    @Test(expected = ConflictingAnnotations::class)
    fun `test combined id and property on property annotation`() {
        @Vertex("test") class Vert(@ID @Property("a") val a: String?)
        VertexDescription(Vert::class)
    }

    @Test(expected = IDParameterRequired::class)
    fun `test duplicate id on property`() {
        @Vertex("test") class Vert(@ID val id1: String?, @ID val id2: String?)
        VertexDescription(Vert::class)
    }

    @Test(expected = DuplicateToVertex::class)
    fun `test duplicate to-vertex on param`() {
        class Edge(@ID val id: String?, @FromVertex val from: Any, @ToVertex val to1: Any, @ToVertex val to2: Any)
        EdgeDescription("", Edge::class)
    }

    @Test(expected = DuplicateFromVertex::class)
    fun `test duplicate from-vertex on param`() {
        class Edge(@ID val id: String?, @FromVertex val from1: Any, @FromVertex val from2: Any, @ToVertex val to: Any)
        EdgeDescription("", Edge::class)
    }

    @Test(expected = NonNullableID::class)
    fun `test non-nullable id property`() {
        @Vertex("test") class Vert(@ID val id: String)
        VertexDescription(Vert::class)
    }

    @Test(expected = ToVertexParameterMissing::class)
    fun `test to-vertex param missing`() {
        class Edge(@ID val id: String?, @FromVertex val from: Any)
        EdgeDescription("", Edge::class)
    }

    @Test(expected = FromVertexParameterMissing::class)
    fun `test out-vertex param missing`() {
        class Edge(@ID val id: String?, @ToVertex val to: Any)
        EdgeDescription("", Edge::class)
    }

    @Test(expected = IDParameterRequired::class)
    fun `test id property missing`() {
        @Vertex("test") class Vert(val id: String?)
        VertexDescription(Vert::class)
    }

    @Test(expected = MapperUnsupported::class)
    fun `test id mapper unsupported`() {
        @Vertex("test") class Vert(@ID @Mapper(Base64Mapper::class) val id: String?)
        VertexDescription(Vert::class)
    }

    @Test(expected = MapperUnsupported::class)
    fun `test to-vertex mapper unsupported`() {
        class Edge(@ID val id: String?, @ToVertex @Mapper(Base64Mapper::class) val to: Any, @FromVertex val from: Any)
        EdgeDescription("", Edge::class)
    }

    @Test(expected = MapperUnsupported::class)
    fun `test from-vertex mapper unsupported`() {
        class Edge(@ID val id: String?, @ToVertex val to: Any, @FromVertex @Mapper(Base64Mapper::class) val from: Any)
        EdgeDescription("", Edge::class)
    }

    @Test(expected = DuplicatePropertyName::class)
    fun `test duplicate param name`() {
        @Vertex("test") class Vert(@ID val id: String?, @Property("a") val a: String, @Property("a") val b: String)
        VertexDescription(Vert::class)
    }

    @Ignore
    @Test(expected = ReservedIDName::class)
    fun `test reserved id name`() {
        @Vertex("test") class Vert(@ID val id: String?, @Property(idTag) val a: String)
        VertexDescription(Vert::class)
    }

    @Ignore
    @Test(expected = ReservedNestedPropertyDelimiter::class)
    fun `test reserved nested property delimiter`() {
        @Vertex("test") class Vert(@ID val id: String?, @Property("a$nestedPropertyDelimiter") val a: String)
        VertexDescription(Vert::class)
    }

    @Ignore
    @Test(expected = ReservedNumberKey::class)
    fun `test reserved number key`() {
        @Vertex("test") class Vert(@ID val id: String?, @Property("23") val a: String)
        VertexDescription(Vert::class)
    }

    @Test(expected = ClassInheritanceMismatch::class)
    fun `test mapper type incompatible`() {
        @Vertex("test")
        class Vert(
                @ID val id: String?,
                @Property("a") val a: String,
                @Property("b") @Mapper(LongToStringMapper::class) val b: String)
        VertexDescription(Vert::class)
    }

    @Test(expected = NonNullableNonOptionalParameter::class)
    fun `test non nullable non optional parameter annotated`() {
        @Vertex("test") class Vert(@ID val id: String?, val a: String)
        VertexDescription(Vert::class)
    }

    @Test
    fun `test object description`() {
        @Vertex("test")
        class Vert(
                @ID
                val id: String?,

                @Property("a")
                @Mapper(LongToStringMapper::class)
                val a: Long,

                b: String?)
        val description = VertexDescription(Vert::class)

        assertThat(description.id).isNotNull
        assertThat(description.id.kClass).isEqualTo(String::class)
        assertThat(description.id.mapper).isNull()
        assertThat(description.id.property.name).isEqualTo("id")
        assertThat(description.id.parameter.name).isEqualTo("id")

        val properties = description.properties
        assertThat(properties).hasSize(1)

        val property = properties.entries.single()
        assertThat(property.key).isEqualTo("a")
        assertThat(property.value.parameter.name).isEqualTo("a")
        assertThat(property.value.property.name).isEqualTo("a")
        assertThat(property.value.kClass).isEqualTo(Long::class)
        assertThat(property.value.mapper).isNotNull

        assertThat(description.nullConstructorParameters).hasSize(1)
        val nullParam = description.nullConstructorParameters.single()
        assertThat(nullParam.name).isEqualTo("b")
    }

    @Test
    fun `test object description with separate properties`() {
        @Vertex("test")
        class Vert(
                @ID
                val id: String?,

                @Property("a")
                val a: String,

                @Property("b")
                b: String) {
            @Property("a") val lowercaseA get() = a.toLowerCase()
            @Property("b") val lowercaseB = b.toLowerCase()
        }
        val description = VertexDescription(Vert::class)
        val properties = description.properties
        assertThat(properties).hasSize(2)
    }
}
