@file:Suppress("unused", "UNUSED_PARAMETER")

package org.apache.tinkerpop.gremlin.ogm.reflection

import org.apache.tinkerpop.gremlin.ogm.GraphMapper.Companion.idTag
import org.apache.tinkerpop.gremlin.ogm.annotations.*
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.*
import org.apache.tinkerpop.gremlin.ogm.extensions.nestedPropertyDelimiter
import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import util.example.Base64Mapper
import util.example.asymmetricManyToMany

internal class DescriptionBuilderTest {

    class LongToStringMapper : PropertyBiMapper<Long, String> {
        override fun forwardMap(from: Long) = from.toString()
        override fun inverseMap(from: String) = from.toLong()
    }



    @Test(expected = PrimaryConstructorMissing::class)
    fun `test no primary constructor`() {
        VertexDescription(Vertex::class)
    }

    @Test(expected = ConflictingAnnotations::class)
    fun `test combined id and property on property annotation`() {
        @Element("test") class Vert(@ID @Property("a") val a: String?) : Vertex
        VertexDescription(Vert::class)
    }

    @Test(expected = IDParameterRequired::class)
    fun `test duplicate id on property`() {
        @Element("test") class Vert(@ID val id1: String?, @ID val id2: String?) : Vertex
        VertexDescription(Vert::class)
    }

    @Test(expected = DuplicateToVertex::class)
    fun `test duplicate to-vertex on param`() {
        @Element("test") class E(
                @ID val id: String?,
                @FromVertex override val from: Vertex,
                @ToVertex override val to: Vertex,
                @ToVertex val to2: Vertex) : Edge<Vertex, Vertex>
        EdgeDescription(asymmetricManyToMany, E::class)
    }

    @Test(expected = DuplicateFromVertex::class)
    fun `test duplicate from-vertex on param`() {
        @Element("test") class E(
                @ID val id: String?,
                @FromVertex override val from: Vertex,
                @FromVertex val from2: Any,
                @ToVertex override val to: Vertex
        ) : Edge<Vertex, Vertex>
        EdgeDescription(asymmetricManyToMany, E::class)
    }

    @Test(expected = NonNullableID::class)
    fun `test non-nullable id property`() {
        @Element("test") class Vert(@ID val id: String) : Vertex
        VertexDescription(Vert::class)
    }
    
    @Test(expected = IDParameterRequired::class)
    fun `test id property missing`() {
        @Element("test") class Vert(val id: String?) : Vertex
        VertexDescription(Vert::class)
    }

    @Test(expected = MapperUnsupported::class)
    fun `test id mapper unsupported`() {
        @Element("test") class Vert(@ID @Mapper(Base64Mapper::class) val id: String?) : Vertex
        VertexDescription(Vert::class)
    }

    @Test(expected = MapperUnsupported::class)
    fun `test to-vertex mapper unsupported`() {
        @Element("test") class E(
                @ID val id: String?,
                @ToVertex @Mapper(Base64Mapper::class)
                override val to: Vertex,
                @FromVertex override val from: Vertex
        ) : Edge<Vertex, Vertex>
        EdgeDescription(asymmetricManyToMany, E::class)
    }

    @Test(expected = MapperUnsupported::class)
    fun `test from-vertex mapper unsupported`() {
        @Element("test") class E(
                @ID val id: String?,
                @ToVertex override val to: Vertex,
                @FromVertex @Mapper(Base64Mapper::class) override val from: Vertex
        ) : Edge<Vertex, Vertex>
        EdgeDescription(asymmetricManyToMany, E::class)
    }

    @Test(expected = DuplicatePropertyName::class)
    fun `test duplicate param name`() {
        @Element("test") class Vert(@ID val id: String?, @Property("a") val a: String, @Property("a") val b: String) : Vertex
        VertexDescription(Vert::class)
    }

    @Test(expected = ReservedIDName::class)
    fun `test reserved id name`() {
        @Element("test") class Vert(@ID val id: String?, @Property(idTag) val a: String) : Vertex
        VertexDescription(Vert::class)
    }

    @Test(expected = ReservedNestedPropertyDelimiter::class)
    fun `test reserved nested property delimiter`() {
        @Element("test") class Vert(@ID val id: String?, @Property("a$nestedPropertyDelimiter") val a: String) : Vertex
        VertexDescription(Vert::class)
    }

    @Test(expected = ReservedNumberKey::class)
    fun `test reserved number key`() {
        @Element("test") class Vert(@ID val id: String?, @Property("23") val a: String) : Vertex
        VertexDescription(Vert::class)
    }

    @Test(expected = ClassInheritanceMismatch::class)
    fun `test mapper type incompatible`() {
        @Element("test")
        class Vert(
                @ID val id: String?,
                @Property("a") val a: String,
                @Property("b") @Mapper(LongToStringMapper::class) val b: String
        ) : Vertex
        VertexDescription(Vert::class)
    }

    @Test(expected = NonNullableNonOptionalParameter::class)
    fun `test non nullable non optional parameter annotated`() {
        @Element("test") class Vert(@ID val id: String?, val a: String) : Vertex
        VertexDescription(Vert::class)
    }

    @Test
    fun `test object description`() {
        @Element("test")
        class Vert(
                @ID
                val id: String?,

                @Property("a")
                @Mapper(LongToStringMapper::class)
                val a: Long,

                b: String?
        ) : Vertex
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
        @Element("test")
        class Vert(
                @ID
                val id: String?,

                @Property("a")
                val a: String,

                @Property("b")
                b: String
        ) : Vertex {
            @Property("a") val lowercaseA get() = a.toLowerCase()
            @Property("b") val lowercaseB = b.toLowerCase()
        }
        val description = VertexDescription(Vert::class)
        val properties = description.properties
        assertThat(properties).hasSize(2)
    }
}
