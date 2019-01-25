package org.apache.tinkerpop.gremlin.ogm

import org.apache.tinkerpop.gremlin.ogm.annotations.Element
import org.apache.tinkerpop.gremlin.ogm.annotations.ID
import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import org.apache.tinkerpop.gremlin.ogm.annotations.defaults.DefaultValue
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.exceptions.ConflictingEdge
import org.apache.tinkerpop.gremlin.ogm.exceptions.MissingEdge
import org.apache.tinkerpop.gremlin.ogm.exceptions.ObjectNotSaved
import org.apache.tinkerpop.gremlin.ogm.steps.*
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec.EdgeSpec
import org.apache.tinkerpop.gremlin.ogm.reflection.CachedGraphDescription
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.ogm.steps.edgestep.outE
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.internal.bytebuddy.utility.RandomString
import org.junit.Before
import org.junit.Test
import util.example.*
import util.example.IntToBoolEdge.Companion.fromIntToBool
import java.util.*

internal class GraphMapperTest {

    private lateinit var gm: GraphMapper

    @Before
    fun setup() {
        gm = exampleGraphMapper()
    }

    @Test
    fun `test save and load VertexWithBoolean`() {
        saveAndLoadVertex(VertexWithBoolean.sample())
    }

    @Test
    fun `test save and load VertexWithByte`() {
        saveAndLoadVertex(VertexWithByte.sample())
    }

    @Test
    fun `test save and load VertexWithInt`() {
        saveAndLoadVertex(VertexWithInt.sample())
    }

    @Test
    fun `test save and load VertexWithDouble`() {
        saveAndLoadVertex(VertexWithDouble.sample())
    }

    @Test
    fun `test save and load VertexWithFloat`() {
        saveAndLoadVertex(VertexWithFloat.sample())
    }

    @Test
    fun `test save and load VertexWithString`() {
        saveAndLoadVertex(VertexWithString.sample())
    }

    @Test
    fun `test save and load VertexWithInstant`() {
        saveAndLoadVertex(VertexWithInstant.sample())
    }

    @Test
    fun `test save and load VertexWithURL`() {
        saveAndLoadVertex(VertexWithURL.sample())
    }

    @Test
    fun `test save and load VertexWithLong`() {
        saveAndLoadVertex(VertexWithLong.sample())
    }

    @Test
    fun `test save and load VertexWithDoubleNested`() {
        saveAndLoadVertex(VertexWithDoubleNested.sample())
    }

    @Test
    fun `test save and load VertexWithObjectList`() {
        saveAndLoadVertex(VertexWithObjectList.sample())
    }

    @Test
    fun `test save and load VertexWithObjectMap`() {
        saveAndLoadVertex(VertexWithObjectMap.sample())
    }

    @Test
    fun `test save and load VertexWithPrimitiveSet`() {
        saveAndLoadVertex(VertexWithPrimitiveSet.sample())
    }

    @Test
    fun `test save and load VertexWithPrimitiveSet empty set`() {
        saveAndLoadVertex(VertexWithPrimitiveSet(setOfStrings = setOf()))
    }

    @Test
    fun `test save and load VertexWithPrimitiveList`() {
        saveAndLoadVertex(VertexWithPrimitiveList.sample())
    }

    @Test
    fun `test save and load VertexWithPrimitiveList empty list`() {
        saveAndLoadVertex(VertexWithPrimitiveList(listOfInts = listOf()))
    }

    @Test
    fun `test save and load VertexWithPrimitiveMap`() {
        saveAndLoadVertex(VertexWithPrimitiveMap.sample())
    }

    @Test
    fun `test save and load VertexWithPrimitiveMap empty map`() {
        saveAndLoadVertex(VertexWithPrimitiveMap(intMap = mapOf()))
    }

    @Test
    fun `test save and load VertexWithNullablePrimitiveMap null map`() {
        saveAndLoadVertex(VertexWithNullablePrimitiveMap.sample())
    }

    @Test
    fun `test save and load VertexWithEnum`() {
        saveAndLoadVertex(VertexWithEnum.sample())
    }

    @Test
    fun `test save and load VertexWithNumber`() {
        saveAndLoadVertex(VertexWithNumber.sample())
    }

    @Test
    fun `test save and load VertexWithCustomMapper`() {
        saveAndLoadVertex(VertexWithCustomMapper.sample())
    }

    @Test
    fun `test save and load VertexWithNullable`() {
        saveAndLoadVertex(VertexWithNullable.sample())
    }

    @Test
    fun `test save and load VertexWithNullable nonnull`() {
        saveAndLoadVertex(VertexWithNullable(nullableString = RandomString.make()))
    }

    @Test
    fun `test save and load VertexWithUUID`() {
        saveAndLoadVertex(VertexWithUUID.sample())
    }

    @Test
    fun `test save and load vertex with default value supplier`() {

        @Element(label = "VertexWithDefaultValue")
        class VertexWithNullable(
                @ID
                id: Long? = null,

                @Property(key = "a")
                string: String?
        ) : BaseVertex<String?>(id = id, a = string)

        @Element(label = "VertexWithDefaultValue")
        class VertexWithDefault(
                @ID
                id: Long? = null,

                @Property(key = "a")
                @DefaultValue(DefaultStringSupplier::class)
                string: String
        ) : BaseVertex<String>(id = id, a = string)

        val gm1 = object : GraphMapper {
            override val graphDescription: GraphDescription get() = CachedGraphDescription(vertices = setOf(VertexWithNullable::class))
            override val g: GraphTraversalSource get() = gm.g
        }

        val gm2 = object : GraphMapper {
            override val graphDescription: GraphDescription get() = CachedGraphDescription(vertices = setOf(VertexWithDefault::class))
            override val g: GraphTraversalSource get() = gm.g
        }

        val defaultStringSupplier = DefaultStringSupplier()

        val vertexWithNull = VertexWithNullable(string = null)
        val saved = gm1.saveV(vertexWithNull)
        assertThat(saved.id).isNotNull()
        assertThat(saved.a).isNull()

        val loaded = gm2.V<VertexWithDefault>(saved.id!!)
        assertThat(loaded).isNotNull
        assertThat(loaded!!.id).isEqualTo(saved.id)
        assertThat(loaded.a).isEqualTo(defaultStringSupplier.get())
    }

    @Test
    fun `test save and load VertexWithTransient`() {
        val v = VertexWithTransient.sample()
        assertThat(v.id).isNull()
        assertThat(v.transientString).isNotNull()

        val saved = gm.saveV(v)
        assertThat(saved.id).isNotNull()
        assertThat(saved.transientString).isNull()

        val loaded = gm.V<VertexWithTransient>(saved.id!!)
        assertThat(loaded).isNotNull
        assertThat(loaded!!.id).isEqualTo(saved.id)
        assertThat(loaded.transientString).isNull()
    }

    @Test(expected = ObjectNotSaved::class)
    fun `test save and load IntToBoolEdge vertices not saved`() {
        gm.saveE(IntToBoolEdge(
                a = RandomString.make(),
                from = VertexWithInt.sample(),
                to = VertexWithBoolean.sample()))
    }

    @Test
    fun `test save and load IntToBoolEdge`() {
        val edge = IntToBoolEdge(
                a = RandomString.make(),
                from = gm.saveV(VertexWithInt.sample()),
                to = gm.saveV(VertexWithBoolean.sample()))

        assertThat(edge.id).isNull()

        val saved = gm.saveE(edge)
        assertThat(saved.id).isNotNull()
        assertThat(saved.a).isEqualTo(edge.a)
        assertThat(saved.to).isEqualTo(edge.to)
        assertThat(saved.from).isEqualTo(edge.from)

        val loaded = gm.E<VertexWithInt, VertexWithBoolean, IntToBoolEdge>(saved.id!!)
        assertThat(loaded).isNotNull
        assertThat(loaded!!.id).isEqualTo(saved.id)
        assertThat(loaded.a).isEqualTo(saved.a)
        assertThat(loaded.to).isEqualTo(saved.to)
        assertThat(loaded.from).isEqualTo(saved.from)
    }

    @Test
    fun `test load all`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val objs = gm.allV<VertexWithInt>()
        assertThat(objs).isEqualTo(listOf(a, b))
    }

    private inline fun <reified T : BaseVertex<*>> saveAndLoadVertex(vertex: T) {
        assertThat(vertex.id).isNull()

        val saved = gm.saveV(vertex)
        assertThat(saved.id).isNotNull()
        assertThat(saved.a).isEqualTo(vertex.a)

        val loaded = gm.V<T>(saved.id!!)
        assertThat(loaded).isNotNull
        assertThat(loaded!!.id).isEqualTo(saved.id)
        assertThat(loaded.a).isEqualTo(saved.a)
    }

    @Test
    fun `test traverse AsymmetricManyToMany relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        traverse(from = a, step = asymmetricManyToMany, expecting = listOf())
        traverse(from = b, step = asymmetricManyToMany, expecting = listOf())
        traverse(from = b, step = asymmetricManyToMany.inverse, expecting = listOf())
        traverse(from = c, step = asymmetricManyToMany.inverse, expecting = listOf())

        gm.saveE(asymmetricManyToMany from a to listOf(b, c))
        gm.saveE(asymmetricManyToMany from b to c)

        traverse(from = a, step = asymmetricManyToMany, expecting = listOf(b, c))
        traverse(from = b, step = asymmetricManyToMany, expecting = listOf(c))
        traverse(from = c, step = asymmetricManyToMany, expecting = listOf())

        traverse(from = a, step = asymmetricManyToMany.inverse, expecting = listOf())
        traverse(from = b, step = asymmetricManyToMany.inverse, expecting = listOf(a))
        traverse(from = c, step = asymmetricManyToMany.inverse, expecting = listOf(a, b))
    }

    @Test
    fun `test duplicate AsymmetricManyToMany relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricManyToMany from a to b)
        gm.saveE(asymmetricManyToMany from a to b)

        traverse(from = a, step = asymmetricManyToMany, expecting = listOf(b))
    }

    @Test
    fun `test traverse AsymmetricOptionalToMany relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        traverse(from = a, step = asymmetricOptionalToMany, expecting = listOf())

        gm.saveE(asymmetricOptionalToMany from a to listOf(b, c))

        traverse(from = a, step = asymmetricOptionalToMany, expecting = listOf(b, c))
        traverse(from = b, step = asymmetricOptionalToMany, expecting = listOf())
        traverse(from = c, step = asymmetricOptionalToMany, expecting = listOf())
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricOptionalToMany relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToMany from a to b)
        gm.saveE(asymmetricOptionalToMany from a to b)
        gm.saveE(asymmetricOptionalToMany from c to b)
    }

    @Test
    fun `test duplicate AsymmetricOptionalToMany relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToMany from a to b)
        gm.saveE(asymmetricOptionalToMany from a to b)

        traverse(from = a, step = asymmetricOptionalToMany, expecting = listOf(b))
    }

    @Test
    fun `test traverse AsymmetricManyToOptional relationship`() {
        val asymmetricManyToOptional: EdgeSpec.ManyToOptional<VertexWithInt, VertexWithInt> =
                asymmetricOptionalToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        traverse(from = a, step = asymmetricManyToOptional, expecting = null)
        traverse(from = b, step = asymmetricManyToOptional, expecting = null)

        gm.saveE(asymmetricManyToOptional from listOf(a, b) to c)

        traverse(from = a, step = asymmetricManyToOptional, expecting = c)
        traverse(from = b, step = asymmetricManyToOptional, expecting = c)
        traverse(from = c, step = asymmetricManyToOptional, expecting = null)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricManyToOptional relationship conflicting edge to vertex`() {
        val asymmetricManyToOptional: EdgeSpec.ManyToOptional<VertexWithInt, VertexWithInt> =
                asymmetricOptionalToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricManyToOptional from a to b)
        gm.saveE(asymmetricManyToOptional from a to c)
    }

    @Test
    fun `test duplicate AsymmetricManyToOptional relationship is no-op`() {
        val asymmetricManyToOptional: EdgeSpec.ManyToOptional<VertexWithInt, VertexWithInt> =
                asymmetricOptionalToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricManyToOptional from a to b)
        gm.saveE(asymmetricManyToOptional from a to b)

        traverse(from = a, step = asymmetricManyToOptional, expecting = b)
    }

    @Test
    fun `test traverse AsymmetricSingleToMany relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToMany from a to listOf(b, c))

        traverse(from = a, step = asymmetricSingleToMany, expecting = listOf(b, c))
        traverse(from = b, step = asymmetricSingleToMany, expecting = listOf())
        traverse(from = c, step = asymmetricSingleToMany, expecting = listOf())
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricSingleToMany relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToMany from a to b)
        gm.saveE(asymmetricSingleToMany from c to b)
    }

    @Test
    fun `test duplicate AsymmetricSingleToMany relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToMany from a to b)
        gm.saveE(asymmetricSingleToMany from a to b)

        traverse(from = a, step = asymmetricSingleToMany, expecting = listOf(b))
    }

    @Test
    fun `test traverse AsymmetricManyToSingle relationship`() {
        val asymmetricManyToSingle: EdgeSpec.ManyToSingle<VertexWithInt, VertexWithInt> =
                asymmetricSingleToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricManyToSingle from listOf(a, b) to c)

        traverse(from = a, step = asymmetricManyToSingle, expecting = c)
        traverse(from = b, step = asymmetricManyToSingle, expecting = c)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricManySingle relationship conflicting edge to vertex`() {
        val asymmetricManyToSingle: EdgeSpec.ManyToSingle<VertexWithInt, VertexWithInt> =
                asymmetricSingleToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricManyToSingle from a to b)
        gm.saveE(asymmetricManyToSingle from a to c)
    }

    @Test(expected = MissingEdge::class)
    fun `test traverse AsymmetricManySingle relationship missing to vertex`() {
        val asymmetricManyToSingle: EdgeSpec.ManyToSingle<VertexWithInt, VertexWithInt> =
                asymmetricSingleToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        gm.traverse(asymmetricManyToSingle from a)
    }

    @Test
    fun `test duplicate AsymmetricManySingle relationship is no-op`() {
        val asymmetricManyToSingle: EdgeSpec.ManyToSingle<VertexWithInt, VertexWithInt> =
                asymmetricSingleToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricManyToSingle from a to b)
        gm.saveE(asymmetricManyToSingle from a to b)

        traverse(from = a, step = asymmetricManyToSingle, expecting = b)
    }

    @Test
    fun `test traverse AsymmetricSingleToSingle relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToSingle from a to b)
        gm.saveE(asymmetricSingleToSingle from b to c)
        gm.saveE(asymmetricSingleToSingle from c to a)

        traverse(from = a, step = asymmetricSingleToSingle, expecting = b)
        traverse(from = b, step = asymmetricSingleToSingle, expecting = c)
        traverse(from = c, step = asymmetricSingleToSingle, expecting = a)

        traverse(from = a, step = asymmetricSingleToSingle.inverse, expecting = c)
        traverse(from = b, step = asymmetricSingleToSingle.inverse, expecting = a)
        traverse(from = c, step = asymmetricSingleToSingle.inverse, expecting = b)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricSingleToSingle relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToSingle from a to b)
        gm.saveE(asymmetricSingleToSingle from a to c)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricSingleToSingle relationship conflicting edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToSingle from a to b)
        gm.saveE(asymmetricSingleToSingle from c to b)
    }

    @Test(expected = MissingEdge::class)
    fun `test traverse AsymmetricSingleToSingle relationship missing edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        gm.traverse(asymmetricSingleToSingle from a)
    }

    @Test
    fun `test duplicate AsymmetricSingleToSingle relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToSingle from a to b)
        gm.saveE(asymmetricSingleToSingle from a to b)

        traverse(from = a, step = asymmetricSingleToSingle, expecting = b)
    }

    @Test
    fun `test traverse AsymmetricSingleToOptional relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        traverse(from = a, step = asymmetricSingleToOptional, expecting = null)

        gm.saveE(asymmetricSingleToOptional from a to b)

        traverse(from = a, step = asymmetricSingleToOptional, expecting = b)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricSingleToOptional relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToOptional from a to b)
        gm.saveE(asymmetricSingleToOptional from a to c)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricSingleToOptional relationship conflicting edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToOptional from a to b)
        gm.saveE(asymmetricSingleToOptional from c to b)
    }

    @Test
    fun `test duplicate AsymmetricSingleToOptional relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToOptional from a to b)
        gm.saveE(asymmetricSingleToOptional from a to b)

        traverse(from = a, step = asymmetricSingleToOptional, expecting = b)
    }

    @Test
    fun `test traverse AsymmetricOptionalToSingle relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToSingle from a to b)

        traverse(from = a, step = asymmetricOptionalToSingle, expecting = b)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricOptionalToSingle relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToSingle from a to b)
        gm.saveE(asymmetricOptionalToSingle from a to c)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricOptionalToSingle relationship conflicting edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToSingle from a to b)
        gm.saveE(asymmetricOptionalToSingle from c to b)
    }

    @Test(expected = MissingEdge::class)
    fun `test traverse AsymmetricOptionalToSingle relationship missing edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        gm.traverse(asymmetricOptionalToSingle from a)
    }

    @Test
    fun `test duplicate AsymmetricOptionalToSingle relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToSingle from a to b)
        gm.saveE(asymmetricOptionalToSingle from a to b)

        traverse(from = a, step = asymmetricOptionalToSingle, expecting = b)
    }

    @Test
    fun `test traverse AsymmetricOptionalToOptional relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        traverse(from = a, step = asymmetricOptionalToOptional, expecting = null)

        gm.saveE(asymmetricOptionalToOptional from a to b)

        traverse(from = a, step = asymmetricOptionalToOptional, expecting = b)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricOptionalToOptional relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToOptional from a to b)
        gm.saveE(asymmetricOptionalToOptional from a to c)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricOptionalToOptional relationship conflicting edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToOptional from a to b)
        gm.saveE(asymmetricOptionalToOptional from c to b)
    }

    @Test
    fun `test duplicate AsymmetricOptionalToOptional relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToOptional from a to b)
        gm.saveE(asymmetricOptionalToOptional from a to b)

        traverse(from = a, step = asymmetricOptionalToOptional, expecting = b)
    }

    @Test
    fun `test traverse SymmetricOptionalToOptional relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        traverse(from = a, step = symmetricOptionalToOptional, expecting = null)
        traverse(from = b, step = symmetricOptionalToOptional, expecting = null)

        traverse(from = a, step = symmetricOptionalToOptional.inverse, expecting = null)
        traverse(from = b, step = symmetricOptionalToOptional.inverse, expecting = null)

        gm.saveE(symmetricOptionalToOptional from a to b)

        traverse(from = a, step = symmetricOptionalToOptional, expecting = b)
        traverse(from = b, step = symmetricOptionalToOptional, expecting = a)

        traverse(from = a, step = symmetricOptionalToOptional.inverse, expecting = b)
        traverse(from = b, step = symmetricOptionalToOptional.inverse, expecting = a)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save SymmetricOptionalToOptional relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(symmetricOptionalToOptional from a to b)
        gm.saveE(symmetricOptionalToOptional from a to c)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save SymmetricOptionalToOptional relationship conflicting edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(symmetricOptionalToOptional from a to b)
        gm.saveE(symmetricOptionalToOptional from c to b)
    }

    @Test
    fun `test duplicate SymmetricOptionalToOptional relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(symmetricOptionalToOptional from a to b)
        gm.saveE(symmetricOptionalToOptional from b to a)

        traverse(from = a, step = symmetricOptionalToOptional, expecting = b)
        traverse(from = b, step = symmetricOptionalToOptional, expecting = a)

        traverse(from = a, step = symmetricOptionalToOptional.inverse, expecting = b)
        traverse(from = b, step = symmetricOptionalToOptional.inverse, expecting = a)
    }

    @Test
    fun `test traverse SymmetricSingleToSingle relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(symmetricSingleToSingle from a to b)

        traverse(from = a, step = symmetricSingleToSingle, expecting = b)
        traverse(from = b, step = symmetricSingleToSingle, expecting = a)

        traverse(from = a, step = symmetricSingleToSingle.inverse, expecting = b)
        traverse(from = b, step = symmetricSingleToSingle.inverse, expecting = a)
    }

    @Test
    fun `test duplicate SymmetricSingleToSingle relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(symmetricSingleToSingle from a to b)
        gm.saveE(symmetricSingleToSingle from b to a)

        traverse(from = a, step = symmetricSingleToSingle, expecting = b)
        traverse(from = b, step = symmetricSingleToSingle, expecting = a)

        traverse(from = a, step = symmetricSingleToSingle.inverse, expecting = b)
        traverse(from = b, step = symmetricSingleToSingle.inverse, expecting = a)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save SymmetricSingleToSingle relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(symmetricSingleToSingle from a to b)
        gm.saveE(symmetricSingleToSingle from a to c)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save SymmetricSingleToSingle relationship conflicting edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(symmetricSingleToSingle from a to b)
        gm.saveE(symmetricSingleToSingle from c to b)
    }

    @Test(expected = MissingEdge::class)
    fun `test traverse SymmetricSingleToSingle relationship missing edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        gm.traverse(symmetricSingleToSingle from a)
    }

    @Test
    fun `test traverse SymmetricManyToMany relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(symmetricManyToMany from a to listOf(b, c))
        gm.saveE(symmetricManyToMany from b to c)

        traverse(from = a, step = symmetricManyToMany, expecting = listOf(b, c))
        traverse(from = b, step = symmetricManyToMany, expecting = listOf(c, a))
        traverse(from = c, step = symmetricManyToMany, expecting = listOf(a, b))

        traverse(from = a, step = symmetricManyToMany.inverse, expecting = listOf(b, c))
        traverse(from = b, step = symmetricManyToMany.inverse, expecting = listOf(c, a))
        traverse(from = c, step = symmetricManyToMany.inverse, expecting = listOf(a, b))
    }

    @Test
    fun `test duplicate SymmetricManyToMany relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(symmetricManyToMany from a to b)
        gm.saveE(symmetricManyToMany from b to a)

        traverse(from = a, step = symmetricManyToMany, expecting = listOf(b))
        traverse(from = b, step = symmetricManyToMany, expecting = listOf(a))

        traverse(from = a, step = symmetricManyToMany.inverse, expecting = listOf(b))
        traverse(from = b, step = symmetricManyToMany.inverse, expecting = listOf(a))
    }

    private fun <FROM : Vertex, TO : Any> traverse(from: FROM, step: Step.ToSingle<FROM, TO>, expecting: TO) =
            assertThat(gm.traverse(step from from)).isEqualTo(expecting)

    private fun <FROM : Vertex, TO : Any> traverse(from: FROM, step: Step.ToMany<FROM, TO>, expecting: List<TO>) =
            assertThat(gm.traverse(step from from)).isEqualTo(expecting)

    private fun <FROM : Vertex, TO : Any> traverse(from: FROM, step: Step.ToOptional<FROM, TO>, expecting: TO?) =
            assertThat(gm.traverse(step from from)).isEqualTo(expecting)

    @Test
    fun `test traverse ManyToMany to SingleToOptional connection (ManyToMany)`() {
        val manyToManyConnection: Relationship.ManyToMany<VertexWithInt, VertexWithInt> = asymmetricManyToMany link asymmetricSingleToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricManyToMany from a to listOf(b, c))
        gm.saveE(asymmetricManyToMany from b to c)

        traverse(from = a, step = manyToManyConnection, expecting = listOf())
        traverse(from = b, step = manyToManyConnection, expecting = listOf())
        traverse(from = c, step = manyToManyConnection, expecting = listOf())
        traverse(from = x, step = manyToManyConnection, expecting = listOf())

        gm.saveE(asymmetricSingleToOptional from b to x)

        traverse(from = a, step = manyToManyConnection, expecting = listOf(x))
        traverse(from = b, step = manyToManyConnection, expecting = listOf())
        traverse(from = c, step = manyToManyConnection, expecting = listOf())
        traverse(from = x, step = manyToManyConnection, expecting = listOf())
    }

    @Test
    fun `test traverse OptionalToMany to OptionalToMany connection (ManyToMany)`() {
        val manyToManyConnection: Relationship.ManyToMany<VertexWithInt, VertexWithInt> = asymmetricOptionalToMany.inverse link asymmetricOptionalToMany
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToMany.inverse from listOf(a, b) to c)

        traverse(from = a, step = manyToManyConnection, expecting = listOf(a, b))
        traverse(from = b, step = manyToManyConnection, expecting = listOf(a, b))
        traverse(from = c, step = manyToManyConnection, expecting = listOf())
        traverse(from = x, step = manyToManyConnection, expecting = listOf())
        traverse(from = y, step = manyToManyConnection, expecting = listOf())

        gm.saveE(asymmetricOptionalToMany from c to listOf(x, y))

        traverse(from = a, step = manyToManyConnection, expecting = listOf(a, b, x, y))
        traverse(from = b, step = manyToManyConnection, expecting = listOf(a, b, x, y))
        traverse(from = c, step = manyToManyConnection, expecting = listOf())
        traverse(from = x, step = manyToManyConnection, expecting = listOf(a, b, x, y))
        traverse(from = y, step = manyToManyConnection, expecting = listOf(a, b, x, y))
    }

    @Test
    fun `test traverse OptionalToOptional to ManyToMany connection (ManyToMany)`() {
        val manyToManyConnection: Relationship.ManyToMany<VertexWithInt, VertexWithInt> = asymmetricOptionalToOptional link asymmetricManyToMany
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToOptional from a to b)

        traverse(from = a, step = manyToManyConnection, expecting = listOf())
        traverse(from = b, step = manyToManyConnection, expecting = listOf())
        traverse(from = x, step = manyToManyConnection, expecting = listOf())
        traverse(from = y, step = manyToManyConnection, expecting = listOf())

        gm.saveE(asymmetricManyToMany from b to listOf(x, y))

        traverse(from = a, step = manyToManyConnection, expecting = listOf(x, y))
        traverse(from = b, step = manyToManyConnection, expecting = listOf())
        traverse(from = x, step = manyToManyConnection, expecting = listOf())
        traverse(from = y, step = manyToManyConnection, expecting = listOf())
    }

    @Test
    fun `test traverse ManyToMany to ManyToMany connection (ManyToMany)`() {
        val manyToManyConnection: Relationship.ManyToMany<VertexWithInt, VertexWithInt> = asymmetricManyToMany link asymmetricManyToMany
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricManyToMany from a to listOf(b, c))

        traverse(from = a, step = manyToManyConnection, expecting = listOf())
        traverse(from = b, step = manyToManyConnection, expecting = listOf())
        traverse(from = c, step = manyToManyConnection, expecting = listOf())
        traverse(from = x, step = manyToManyConnection, expecting = listOf())
        traverse(from = y, step = manyToManyConnection, expecting = listOf())

        gm.saveE(asymmetricManyToMany from b to c)

        traverse(from = a, step = manyToManyConnection, expecting = listOf(c))
        traverse(from = b, step = manyToManyConnection, expecting = listOf())
        traverse(from = c, step = manyToManyConnection, expecting = listOf())
        traverse(from = x, step = manyToManyConnection, expecting = listOf())
        traverse(from = y, step = manyToManyConnection, expecting = listOf())

        gm.saveE(asymmetricManyToMany from b to listOf(x, y))

        traverse(from = a, step = manyToManyConnection, expecting = listOf(c, x, y))
        traverse(from = b, step = manyToManyConnection, expecting = listOf())
        traverse(from = c, step = manyToManyConnection, expecting = listOf())
        traverse(from = x, step = manyToManyConnection, expecting = listOf())
        traverse(from = y, step = manyToManyConnection, expecting = listOf())

        gm.saveE(asymmetricManyToMany from c to y)

        traverse(from = a, step = manyToManyConnection, expecting = listOf(c, x, y, y))
        traverse(from = b, step = manyToManyConnection, expecting = listOf(y))
        traverse(from = c, step = manyToManyConnection, expecting = listOf())
        traverse(from = x, step = manyToManyConnection, expecting = listOf())
        traverse(from = y, step = manyToManyConnection, expecting = listOf())
    }

    @Test
    fun `test traverse ManyToOptional to OptionalToOptional connection (ManyToOptional)`() {
        val manyToOptionalConnection: Relationship.ManyToOptional<VertexWithInt, VertexWithInt> = asymmetricOptionalToMany.inverse link asymmetricOptionalToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToMany.inverse from listOf(a, b) to c)

        traverse(from = a, step = manyToOptionalConnection, expecting = null)
        traverse(from = b, step = manyToOptionalConnection, expecting = null)
        traverse(from = c, step = manyToOptionalConnection, expecting = null)
        traverse(from = x, step = manyToOptionalConnection, expecting = null)

        gm.saveE(asymmetricOptionalToOptional from c to x)

        traverse(from = a, step = manyToOptionalConnection, expecting = x)
        traverse(from = b, step = manyToOptionalConnection, expecting = x)
        traverse(from = c, step = manyToOptionalConnection, expecting = null)
        traverse(from = x, step = manyToOptionalConnection, expecting = null)
    }

    @Test
    fun `test traverse ManyToSingle to SingleToOptional connection (ManyToOptional)`() {
        val manyToOptionalConnection: Relationship.ManyToOptional<VertexWithInt, VertexWithInt> = asymmetricSingleToMany.inverse link asymmetricSingleToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToMany.inverse from listOf(a, b) to c)

        traverse(from = a, step = manyToOptionalConnection, expecting = null)
        traverse(from = b, step = manyToOptionalConnection, expecting = null)

        gm.saveE(asymmetricSingleToOptional from c to x)

        traverse(from = a, step = manyToOptionalConnection, expecting = x)
        traverse(from = b, step = manyToOptionalConnection, expecting = x)
    }

    @Test
    fun `test traverse SingleToSingle to ManyToOptional connection (ManyToOptional)`() {
        val manyToOptionalConnection: Relationship.ManyToOptional<VertexWithInt, VertexWithInt> = asymmetricSingleToSingle link asymmetricOptionalToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToSingle from a to b)
        gm.saveE(asymmetricSingleToSingle from b to c)
        gm.saveE(asymmetricSingleToSingle from c to a)

        traverse(from = a, step = manyToOptionalConnection, expecting = null)
        traverse(from = b, step = manyToOptionalConnection, expecting = null)
        traverse(from = c, step = manyToOptionalConnection, expecting = null)

        gm.saveE(asymmetricOptionalToMany.inverse from listOf(a, b) to x)

        traverse(from = a, step = manyToOptionalConnection, expecting = x)
        traverse(from = b, step = manyToOptionalConnection, expecting = null)
        traverse(from = c, step = manyToOptionalConnection, expecting = x)
    }

    @Test
    fun `test traverse ManyToSingle to SingleToSingle connection (ManyToSingle)`() {
        val manyToSingleConnection: Relationship.ManyToSingle<VertexWithInt, VertexWithInt> = asymmetricSingleToMany.inverse link symmetricSingleToSingle
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToMany.inverse from listOf(a, b) to c)
        gm.saveE(symmetricSingleToSingle from c to x)

        traverse(from = a, step = manyToSingleConnection, expecting = x)
        traverse(from = b, step = manyToSingleConnection, expecting = x)
    }

    @Test
    fun `test traverse OptionalToSingle to ManyToSingle connection (ManyToSingle)`() {
        val manyToSingleConnection: Relationship.ManyToSingle<VertexWithInt, VertexWithInt> = asymmetricOptionalToSingle link asymmetricSingleToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val d = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToSingle from a to b)
        gm.saveE(asymmetricOptionalToSingle from c to d)
        gm.saveE(asymmetricSingleToMany.inverse from listOf(b, d) to x)

        traverse(from = a, step = manyToSingleConnection, expecting = x)
        traverse(from = c, step = manyToSingleConnection, expecting = x)
    }

    @Test
    fun `test traverse OptionalToMany to SingleToSingle connection (OptionalToMany)`() {
        val optionalToManyConnection: Relationship.OptionalToMany<VertexWithInt, VertexWithInt> = asymmetricOptionalToMany link asymmetricSingleToSingle
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToMany from a to listOf(b, c))

        traverse(from = b, step = optionalToManyConnection, expecting = listOf())
        traverse(from = x, step = optionalToManyConnection, expecting = listOf())
        traverse(from = y, step = optionalToManyConnection, expecting = listOf())

        gm.saveE(asymmetricSingleToSingle from b to x)
        gm.saveE(asymmetricSingleToSingle from c to y)

        traverse(from = a, step = optionalToManyConnection, expecting = listOf(x, y))
        traverse(from = b, step = optionalToManyConnection, expecting = listOf())
        traverse(from = x, step = optionalToManyConnection, expecting = listOf())
        traverse(from = y, step = optionalToManyConnection, expecting = listOf())
    }

    @Test
    fun `test traverse SingleToSingle to OptionalToMany connection (OptionalToMany)`() {
        val optionalToManyConnection: Relationship.OptionalToMany<VertexWithInt, VertexWithInt> = asymmetricSingleToSingle link asymmetricOptionalToMany
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToSingle from a to b)
        gm.saveE(asymmetricSingleToSingle from b to c)
        gm.saveE(asymmetricSingleToSingle from c to a)

        traverse(from = a, step = optionalToManyConnection, expecting = listOf())
        traverse(from = b, step = optionalToManyConnection, expecting = listOf())
        traverse(from = c, step = optionalToManyConnection, expecting = listOf())

        gm.saveE(asymmetricOptionalToMany from a to listOf(x, y))

        traverse(from = a, step = optionalToManyConnection, expecting = listOf())
        traverse(from = b, step = optionalToManyConnection, expecting = listOf())
        traverse(from = c, step = optionalToManyConnection, expecting = listOf(x, y))
    }

    @Test
    fun `test traverse OptionalToSingle to SingleToMany connection (OptionalToMany)`() {
        val optionalToManyConnection: Relationship.OptionalToMany<VertexWithInt, VertexWithInt> = asymmetricOptionalToSingle link asymmetricSingleToMany
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToSingle from a to b)

        traverse(from = a, step = optionalToManyConnection, expecting = listOf())

        gm.saveE(asymmetricSingleToMany from b to listOf(x, y))

        traverse(from = a, step = optionalToManyConnection, expecting = listOf(x, y))
    }

    @Test
    fun `test traverse SingleToOptional to SingleToMany connection (SingleToMany)`() {
        val singleToManyConnection: Relationship.SingleToMany<VertexWithInt, VertexWithInt> = asymmetricSingleToOptional link asymmetricSingleToMany
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToOptional from a to b)

        traverse(from = a, step = singleToManyConnection, expecting = listOf())
        traverse(from = b, step = singleToManyConnection, expecting = listOf())
        traverse(from = x, step = singleToManyConnection, expecting = listOf())
        traverse(from = y, step = singleToManyConnection, expecting = listOf())

        gm.saveE(asymmetricSingleToMany from b to listOf(x, y))

        traverse(from = a, step = singleToManyConnection, expecting = listOf(x, y))
        traverse(from = b, step = singleToManyConnection, expecting = listOf())
        traverse(from = x, step = singleToManyConnection, expecting = listOf())
        traverse(from = y, step = singleToManyConnection, expecting = listOf())
    }

    @Test
    fun `test traverse SingleToMany to SingleToOptional connection (SingleToMany)`() {
        val singleToManyConnection: Relationship.SingleToMany<VertexWithInt, VertexWithInt> = asymmetricSingleToMany link asymmetricSingleToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToMany from a to listOf(b, c))

        traverse(from = a, step = singleToManyConnection, expecting = listOf())
        traverse(from = b, step = singleToManyConnection, expecting = listOf())
        traverse(from = c, step = singleToManyConnection, expecting = listOf())
        traverse(from = x, step = singleToManyConnection, expecting = listOf())
        traverse(from = y, step = singleToManyConnection, expecting = listOf())

        gm.saveE(asymmetricSingleToOptional from b to x)
        gm.saveE(asymmetricSingleToOptional from c to y)

        traverse(from = a, step = singleToManyConnection, expecting = listOf(x, y))
        traverse(from = b, step = singleToManyConnection, expecting = listOf())
        traverse(from = c, step = singleToManyConnection, expecting = listOf())
        traverse(from = x, step = singleToManyConnection, expecting = listOf())
        traverse(from = y, step = singleToManyConnection, expecting = listOf())
    }

    @Test
    fun `test traverse SingleToSingle to SingleToSingle connection (SingleToSingle)`() {
        val singleToSingleConnection: Relationship.SingleToSingle<VertexWithInt, VertexWithInt> = asymmetricSingleToSingle link asymmetricSingleToSingle
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val d = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToSingle from a to b)
        gm.saveE(asymmetricSingleToSingle from b to c)
        gm.saveE(asymmetricSingleToSingle from c to d)
        gm.saveE(asymmetricSingleToSingle from d to a)

        traverse(from = a, step = singleToSingleConnection, expecting = c)
        traverse(from = b, step = singleToSingleConnection, expecting = d)
        traverse(from = c, step = singleToSingleConnection, expecting = a)
    }

    @Test
    fun `test traverse SingleToOptional to SingleToOptional connection (SingleToOptional)`() {
        val singleToOptionalConnection: Relationship.SingleToOptional<VertexWithInt, VertexWithInt> = asymmetricSingleToOptional link asymmetricSingleToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToOptional from a to b)

        traverse(from = a, step = singleToOptionalConnection, expecting = null)
        traverse(from = b, step = singleToOptionalConnection, expecting = null)
        traverse(from = c, step = singleToOptionalConnection, expecting = null)
        traverse(from = x, step = singleToOptionalConnection, expecting = null)

        gm.saveE(asymmetricSingleToOptional from b to c)

        traverse(from = a, step = singleToOptionalConnection, expecting = c)
        traverse(from = b, step = singleToOptionalConnection, expecting = null)
        traverse(from = c, step = singleToOptionalConnection, expecting = null)
        traverse(from = x, step = singleToOptionalConnection, expecting = null)

        gm.saveE(asymmetricSingleToOptional from c to x)

        traverse(from = a, step = singleToOptionalConnection, expecting = c)
        traverse(from = b, step = singleToOptionalConnection, expecting = x)
        traverse(from = c, step = singleToOptionalConnection, expecting = null)
        traverse(from = x, step = singleToOptionalConnection, expecting = null)
    }

    @Test
    fun `test traverse SingleToSingle to SingleToOptional connection (SingleToOptional)`() {
        val singleToOptionalConnection: Relationship.SingleToOptional<VertexWithInt, VertexWithInt> = asymmetricSingleToSingle link asymmetricSingleToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToSingle from a to b)
        gm.saveE(asymmetricSingleToSingle from b to c)
        gm.saveE(asymmetricSingleToSingle from c to a)

        traverse(from = a, step = singleToOptionalConnection, expecting = null)
        traverse(from = b, step = singleToOptionalConnection, expecting = null)
        traverse(from = c, step = singleToOptionalConnection, expecting = null)

        gm.saveE(asymmetricSingleToOptional from a to x)

        traverse(from = a, step = singleToOptionalConnection, expecting = null)
        traverse(from = b, step = singleToOptionalConnection, expecting = null)
        traverse(from = c, step = singleToOptionalConnection, expecting = x)
    }

    @Test
    fun `test traverse SingleToSingle to OptionalToSingle connection (OptionalToSingle)`() {
        val optionalToSingleConnection: Relationship.OptionalToSingle<VertexWithInt, VertexWithInt> = asymmetricSingleToSingle link asymmetricOptionalToSingle
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToSingle from a to b)
        gm.saveE(asymmetricSingleToSingle from b to c)
        gm.saveE(asymmetricSingleToSingle from c to a)
        gm.saveE(asymmetricOptionalToSingle from c to x)

        traverse(from = b, step = optionalToSingleConnection, expecting = x)
    }

    @Test
    fun `test traverse OptionalToSingle to SingleToSingle connection (OptionalToSingle)`() {
        val optionalToSingleConnection: Relationship.OptionalToSingle<VertexWithInt, VertexWithInt> = asymmetricOptionalToSingle link symmetricSingleToSingle
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToSingle from a to b)
        gm.saveE(symmetricSingleToSingle from b to x)

        traverse(from = a, step = optionalToSingleConnection, expecting = x)
    }

    @Test
    fun `test traverse OptionalToSingle to SingleToOptional connection (OptionalToOptional)`() {
        val optionalToOptionalConnection: Relationship.OptionalToOptional<VertexWithInt, VertexWithInt> = asymmetricOptionalToSingle link asymmetricSingleToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricOptionalToSingle from a to b)
        gm.saveE(asymmetricOptionalToSingle from b to c)

        traverse(from = a, step = optionalToOptionalConnection, expecting = null)
        traverse(from = b, step = optionalToOptionalConnection, expecting = null)

        gm.saveE(asymmetricSingleToOptional from c to x)

        traverse(from = a, step = optionalToOptionalConnection, expecting = null)
        traverse(from = b, step = optionalToOptionalConnection, expecting = x)
    }

    @Test
    fun `test traverse OptionalToOptional to OptionalToSingle connection (OptionalToOptional)`() {
        val optionalToOptionalConnection: Relationship.OptionalToOptional<VertexWithInt, VertexWithInt> = symmetricOptionalToOptional link asymmetricOptionalToSingle

        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(symmetricOptionalToOptional from a to b)

        traverse(from = x, step = optionalToOptionalConnection, expecting = null)
        traverse(from = y, step = optionalToOptionalConnection, expecting = null)

        gm.saveE(asymmetricOptionalToSingle from a to x)
        gm.saveE(asymmetricOptionalToSingle from b to y)

        traverse(from = a, step = optionalToOptionalConnection, expecting = y)
        traverse(from = b, step = optionalToOptionalConnection, expecting = x)
        traverse(from = x, step = optionalToOptionalConnection, expecting = null)
        traverse(from = y, step = optionalToOptionalConnection, expecting = null)
    }

    @Test
    fun `test traverse SingleToOptional to OptionalToOptional connection (OptionalToOptional)`() {
        val optionalToOptionalConnection: Relationship.OptionalToOptional<VertexWithInt, VertexWithInt> = asymmetricSingleToOptional link asymmetricOptionalToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(asymmetricSingleToOptional from a to b)
        gm.saveE(asymmetricSingleToOptional from c to a)

        traverse(from = a, step = optionalToOptionalConnection, expecting = null)
        traverse(from = b, step = optionalToOptionalConnection, expecting = null)
        traverse(from = c, step = optionalToOptionalConnection, expecting = null)
        traverse(from = x, step = optionalToOptionalConnection, expecting = null)

        gm.saveE(asymmetricOptionalToOptional from b to x)

        traverse(from = a, step = optionalToOptionalConnection, expecting = x)
        traverse(from = b, step = optionalToOptionalConnection, expecting = null)
        traverse(from = c, step = optionalToOptionalConnection, expecting = null)
        traverse(from = x, step = optionalToOptionalConnection, expecting = null)
    }

    @Test
    fun `test traverse step with map`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        gm.saveE(asymmetricSingleToSingle from a to b)
        val traversalWithMap = asymmetricSingleToSingle.map(VertexWithInt::a)
        traverse(from = a, step = traversalWithMap, expecting = b.a)
    }

    @Test
    fun `test traverse step with flatmap`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        gm.saveE(asymmetricSingleToMany from a to b)
        gm.saveE(asymmetricSingleToMany from a to c)
        val traversalWithFlatMap = asymmetricSingleToMany.flatMap { vertexWithInt ->
            listOf(vertexWithInt.a, vertexWithInt.a * 10)
        }
        traverse(from = a, step = traversalWithFlatMap, expecting = listOf(b.a, b.a * 10, c.a, c.a * 10))
    }

    @Test
    fun `test traverse step with filter`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        gm.saveE(asymmetricSingleToSingle from a to b)

        val traversalWithFilterAll = asymmetricSingleToSingle.filter { false }
        traverse(from = a, step = traversalWithFilterAll, expecting = null)

        val traversalWithFilterNone = asymmetricSingleToSingle.filter { true }
        traverse(from = a, step = traversalWithFilterNone, expecting = b)
    }

    @Test
    fun `test traverse step with filterMap`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        gm.saveE(asymmetricSingleToSingle from a to b)
        val traversalWithEven = asymmetricSingleToSingle.filterMap { vertexWithInt ->
            if (vertexWithInt.a % 2 == 0) vertexWithInt.a else null
        }
        traverse(from = a, step = traversalWithEven, expecting = if (b.a % 2 == 0) b.a else null)
    }

    @Test
    fun `test traverse step with slice`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val d = gm.saveV(VertexWithInt.sample())
        val e = gm.saveV(VertexWithInt.sample())
        gm.saveE(asymmetricSingleToMany from a to listOf(b, c, d, e))
        val traversalWithSlice = asymmetricSingleToMany.slice(range = LongRange(1, 2))
        traverse(from = a, step = traversalWithSlice, expecting = listOf(c, d))
    }

    @Test
    fun `test traverse step with sort`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt(int = 5))
        val c = gm.saveV(VertexWithInt(int = 4))
        val d = gm.saveV(VertexWithInt(int = 3))
        val e = gm.saveV(VertexWithInt(int = 2))
        gm.saveE(asymmetricSingleToMany from a to listOf(b, c, d, e))
        val traversalWithSort = asymmetricSingleToMany.sort(Comparator.comparingInt(VertexWithInt::a))
        traverse(from = a, step = traversalWithSort, expecting = listOf(e, d, c, b))
    }

    @Test
    fun `test traverse step with dedup`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val d = gm.saveV(VertexWithInt.sample())
        gm.saveE(asymmetricSingleToMany from a to listOf(b, c))
        gm.saveE(asymmetricManyToMany.inverse from b to d)
        gm.saveE(asymmetricManyToMany.inverse from c to d)
        val connection = asymmetricSingleToMany link asymmetricManyToMany.inverse
        traverse(from = a, step = connection, expecting = listOf(d, d))
        traverse(from = a, step = connection.dedup(), expecting = listOf(d))
        traverse(from = a, step = connection.map(VertexWithInt::a).dedup(), expecting = listOf(d.a))
    }

    @Test
    fun `test traverse step with outE`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithBoolean.sample())

        val edge = gm.saveE(IntToBoolEdge(a = RandomString.make(), from = a, to = b))

        val traversalResult = gm.traverse(a outE fromIntToBool)
        assertThat(traversalResult).isEqualTo(edge)
    }

    @Test
    fun `test traverse linked step with outE`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithBoolean.sample())

        gm.saveE(asymmetricSingleToSingle from a to b)
        val edge = gm.saveE(IntToBoolEdge(a = RandomString.make(), from = b, to = c))
        traverse(from = a, step = asymmetricSingleToSingle outE fromIntToBool, expecting = edge)
    }
}
