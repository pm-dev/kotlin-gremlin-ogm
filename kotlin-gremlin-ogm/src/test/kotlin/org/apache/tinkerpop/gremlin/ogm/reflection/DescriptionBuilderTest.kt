@file:Suppress("unused", "UNUSED_PARAMETER")

package org.apache.tinkerpop.gremlin.ogm.reflection

import org.apache.tinkerpop.gremlin.ogm.GraphMapper.Companion.idTag
import org.apache.tinkerpop.gremlin.ogm.annotations.*
import org.apache.tinkerpop.gremlin.ogm.exceptions.*
import org.apache.tinkerpop.gremlin.ogm.extensions.nestedPropertyDelimiter
import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import util.example.Base64Mapper

internal class DescriptionBuilderTest {

    class LongToStringMapper : PropertyBiMapper<Long, String> {
        override fun forwardMap(from: Long) = from.toString()
        override fun inverseMap(from: String) = from.toLong()
    }

    interface VertexInterface
    @Test(expected = PrimaryConstructorMissing::class)
    fun `test no primary constructor`() {
        buildObjectDescription(VertexInterface::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = ConflictingAnnotations::class)
    fun `test combined id and property on param annotation`() {
        class Vertex(@param:ID @param:Property("a") val a: String?)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = ConflictingAnnotations::class)
    fun `test combined id and property on property annotation`() {
        class Vertex(@param:ID @property:ID @property:Property("a") val a: String?)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = DuplicateID::class)
    fun `test duplicate id on param`() {
        class Vertex(@param:ID val id1: String?, @param:ID val id2: String?)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = DuplicateID::class)
    fun `test duplicate id on property`() {
        class Vertex(@param:ID @property:ID val id1: String?,
                     @param:ID @property:ID val id2: String?)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = DuplicateInVertex::class)
    fun `test duplicate in-vertex on param`() {
        class Edge(@InVertex val v1: Any,
                   @InVertex val v2: Any)
        buildObjectDescription(Edge::class, type = ObjectDescriptionType.Edge)
    }

    @Test(expected = DuplicateOutVertex::class)
    fun `test duplicate out-vertex on param`() {
        class Edge(@OutVertex val v1: Any,
                   @OutVertex val v2: Any)
        buildObjectDescription(Edge::class, type = ObjectDescriptionType.Edge)
    }

    @Test(expected = NonNullableID::class)
    fun `test non-nullable id param`() {
        class Vertex(@param:ID @property:ID val id: String)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = NonNullableID::class)
    fun `test non-nullable id property`() {
        class Vertex(@param:ID @property:ID val id: String)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = InVertexParameterMissing::class)
    fun `test in-vertex param missing`() {
        class Edge(
                @param:ID @property:ID val id: String?,
                @OutVertex val outV: Any)
        buildObjectDescription(Edge::class, type = ObjectDescriptionType.Edge)
    }

    @Test(expected = OutVertexParameterMissing::class)
    fun `test out-vertex param missing`() {
        class Edge(
                @param:ID @property:ID val id: String?,
                @InVertex val inV: Any)
        buildObjectDescription(Edge::class, type = ObjectDescriptionType.Edge)
    }

    @Test(expected = IDParameterMissing::class)
    fun `test id param missing`() {
        class Vertex(@property:ID val id: String?)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = IDParameterMissing::class)
    fun `test id property missing`() {
        class Vertex(@param:ID val id: String?)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = MapperUnsupported::class)
    fun `test id mapper unsupported`() {
        class Vertex(@param:ID @Mapper(Base64Mapper::class) val id: String?)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = DuplicatePropertyName::class)
    fun `test duplicate param name`() {
        class Vertex(
                @param:ID @property:ID val id: String?,
                @param:Property("a") @property:Property("a") val a: String,
                @param:Property("a") @property:Property("b") val b: String)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = DuplicatePropertyName::class)
    fun `test duplicate property name`() {
        class Vertex(
                @param:ID @property:ID val id: String?,
                @param:Property("a") @property:Property("a") val a: String,
                @param:Property("b") @property:Property("a") val b: String)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = EmptyPropertyName::class)
    fun `test empty property name`() {
        class Vertex(
                @param:ID @property:ID val id: String?,
                @param:Property("") @property:Property("") val a: String)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = ReservedIDName::class)
    fun `test reserved id name`() {
        class Vertex(
                @param:ID @property:ID val id: String?,
                @param:Property(idTag) @property:Property(idTag) val a: String)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = ReservedNestedPropertyDelimiter::class)
    fun `test reserved nested property delimiter`() {
        class Vertex(
                @param:ID @property:ID val id: String?,
                @param:Property("a$nestedPropertyDelimiter") @property:Property("a$nestedPropertyDelimiter") val a: String)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = ReservedNumberKey::class)
    fun `test reserved number key`() {
        class Vertex(
                @param:ID @property:ID val id: String?,
                @param:Property("23") @property:Property("23") val a: String)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = PropertyMissingOnParameter::class)
    fun `test property annotation missing on param`() {
        class Vertex(
                @param:ID @property:ID val id: String?,
                @property:Property("a") val a: String?)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = PropertyMissingOnProperty::class)
    fun `test property annotation missing on property`() {
        class Vertex(
                @param:ID @property:ID val id: String?,
                @param:Property("a") val a: String)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = ClassInheritanceMismatch::class)
    fun `test mapper type incompatible`() {
        class Vertex(
                @param:ID @property:ID val id: String?,
                @param:Property("a") @property:Property("a") val a: String,
                @param:Property("b") @property:Property("b") @Mapper(LongToStringMapper::class) val b: String)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test(expected = NonNullableNonOptionalParameter::class)
    fun `test non nullable non optional parameter annotated`() {
        class Vertex(
                @param:ID @property:ID val id: String?,
                val a: String)
        buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)
    }

    @Test
    fun `test object description`() {
        class Vertex(
                @param:ID @property:ID val id: String?,
                @param:Property("a") @property:Property("a") @Mapper(LongToStringMapper::class) val a: Long,
                b: String?)
        val description = buildObjectDescription(Vertex::class, type = ObjectDescriptionType.Vertex)

        assertThat(description.idDescription).isNotNull
        assertThat(description.idDescription!!.kClass).isEqualTo(String::class)
        assertThat(description.idDescription.mapper).isNull()
        assertThat(description.idDescription.property.name).isEqualTo("id")
        assertThat(description.idDescription.parameter.name).isEqualTo("id")

        val properties = description.objectDescription.properties
        assertThat(properties).hasSize(1)

        val property = properties.entries.single()
        assertThat(property.key).isEqualTo("a")
        assertThat(property.value.parameter.name).isEqualTo("a")
        assertThat(property.value.property.name).isEqualTo("a")
        assertThat(property.value.kClass).isEqualTo(Long::class)
        assertThat(property.value.mapper).isNotNull

        assertThat(description.objectDescription.nullConstructorParameters).hasSize(1)
        val nullParam = description.objectDescription.nullConstructorParameters.single()
        assertThat(nullParam.name).isEqualTo("b")
    }
}
