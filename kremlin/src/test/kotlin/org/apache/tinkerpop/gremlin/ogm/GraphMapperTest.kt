package org.apache.tinkerpop.gremlin.ogm

import org.apache.tinkerpop.gremlin.ogm.exceptions.ConflictingEdge
import org.apache.tinkerpop.gremlin.ogm.exceptions.MissingEdge
import org.apache.tinkerpop.gremlin.ogm.relationships.Connection
import org.apache.tinkerpop.gremlin.ogm.relationships.Relationship
import org.apache.tinkerpop.gremlin.ogm.relationships.`in`
import org.apache.tinkerpop.gremlin.ogm.relationships.bound.from
import org.apache.tinkerpop.gremlin.ogm.relationships.bound.out
import org.apache.tinkerpop.gremlin.ogm.relationships.link
import org.apache.tinkerpop.gremlin.ogm.relationships.steps.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.internal.bytebuddy.utility.RandomString
import org.junit.Before
import org.junit.Test
import util.example.*
import java.util.*

class GraphMapperTest {

    private lateinit var gm: GraphMapper

    @Before
    fun setup() {
        gm = exampleGraphMapper()
    }

    @Test
    fun `test save and load VertexWithBoolean`() {
        saveAndLoad(VertexWithBoolean.sample())
    }

    @Test
    fun `test save and load VertexWithByte`() {
        saveAndLoad(VertexWithByte.sample())
    }

    @Test
    fun `test save and load VertexWithInt`() {
        saveAndLoad(VertexWithInt.sample())
    }

    @Test
    fun `test save and load VertexWithDouble`() {
        saveAndLoad(VertexWithDouble.sample())
    }

    @Test
    fun `test save and load VertexWithFloat`() {
        saveAndLoad(VertexWithFloat.sample())
    }

    @Test
    fun `test save and load VertexWithString`() {
        saveAndLoad(VertexWithString.sample())
    }

    @Test
    fun `test save and load VertexWithInstant`() {
        saveAndLoad(VertexWithInstant.sample())
    }

    @Test
    fun `test save and load VertexWithLong`() {
        saveAndLoad(VertexWithLong.sample())
    }

    @Test
    fun `test save and load VertexWithDoubleNested`() {
        saveAndLoad(VertexWithDoubleNested.sample())
    }

    @Test
    fun `test save and load VertexWithObjectList`() {
        saveAndLoad(VertexWithObjectList.sample())
    }

    @Test
    fun `test save and load VertexWithObjectMap`() {
        saveAndLoad(VertexWithObjectMap.sample())
    }

    @Test
    fun `test save and load VertexWithPrimitiveSet`() {
        saveAndLoad(VertexWithPrimitiveSet.sample())
    }

    @Test
    fun `test save and load VertexWithPrimitiveSet empty set`() {
        saveAndLoad(VertexWithPrimitiveSet(setOfStrings = setOf()))
    }

    @Test
    fun `test save and load VertexWithPrimitiveList`() {
        saveAndLoad(VertexWithPrimitiveList.sample())
    }

    @Test
    fun `test save and load VertexWithPrimitiveList empty list`() {
        saveAndLoad(VertexWithPrimitiveList(listOfInts = listOf()))
    }

    @Test
    fun `test save and load VertexWithPrimitiveMap`() {
        saveAndLoad(VertexWithPrimitiveMap.sample())
    }

    @Test
    fun `test save and load VertexWithPrimitiveMap empty map`() {
        saveAndLoad(VertexWithPrimitiveMap(intMap = mapOf()))
    }

    @Test
    fun `test save and load VertexWithEnum`() {
        saveAndLoad(VertexWithEnum.sample())
    }

    @Test
    fun `test save and load VertexWithNumber`() {
        saveAndLoad(VertexWithNumber.sample())
    }

    @Test
    fun `test save and load VertexWithCustomMapper`() {
        saveAndLoad(VertexWithCustomMapper.sample())
    }

    @Test
    fun `test save and load VertexWithNullable`() {
        saveAndLoad(VertexWithNullable.sample())
    }

    @Test
    fun `test save and load VertexWithNullable nonnull`() {
        saveAndLoad(VertexWithNullable(nullableString = RandomString.make()))
    }

    @Test
    fun `test save and load VertexWithUUID`() {
        saveAndLoad(VertexWithUUID.sample())
    }

    @Test
    fun `test save and load VertexWithTransient`() {
        val v = VertexWithTransient.sample()
        assertThat(v.id).isNull()
        assertThat(v.transientString).isNotNull()

        val saved = gm.saveV(v)
        assertThat(saved.id).isNotNull()
        assertThat(saved.transientString).isNull()

        val loaded = gm.load<VertexWithTransient>(saved.id!!)
        assertThat(loaded).isNotNull
        assertThat(loaded!!.id).isEqualTo(saved.id)
        assertThat(loaded.transientString).isNull()
    }

    @Test
    fun `test load all`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val objs = gm.loadAll<VertexWithInt>()

        assertThat(objs.toList()).isEqualTo(listOf(a, b))
    }

    private inline fun <reified T : Base<*>> saveAndLoad(vertex: T) {
        assertThat(vertex.id).isNull()

        val saved = gm.saveV(vertex)
        assertThat(saved.id).isNotNull()
        assertThat(saved.a).isEqualTo(vertex.a)

        val loaded = gm.load<T>(saved.id!!)
        assertThat(loaded).isNotNull
        assertThat(loaded!!.id).isEqualTo(saved.id)
        assertThat(loaded.a).isEqualTo(saved.a)
    }

    @Test
    fun `test traverse AsymmetricManyToMany relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        traverse(from = a, path = asymmetricManyToMany, expecting = listOf())
        traverse(from = b, path = asymmetricManyToMany, expecting = listOf())
        traverse(from = b, path = asymmetricManyToMany.inverse, expecting = listOf())
        traverse(from = c, path = asymmetricManyToMany.inverse, expecting = listOf())

        gm.saveE(a out asymmetricManyToMany `in` listOf(b, c))
        gm.saveE(b out asymmetricManyToMany `in` c)

        traverse(from = a, path = asymmetricManyToMany, expecting = listOf(b, c))
        traverse(from = b, path = asymmetricManyToMany, expecting = listOf(c))
        traverse(from = c, path = asymmetricManyToMany, expecting = listOf())

        traverse(from = a, path = asymmetricManyToMany.inverse, expecting = listOf())
        traverse(from = b, path = asymmetricManyToMany.inverse, expecting = listOf(a))
        traverse(from = c, path = asymmetricManyToMany.inverse, expecting = listOf(a, b))
    }

    @Test
    fun `test duplicate AsymmetricManyToMany relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricManyToMany `in` b)
        gm.saveE(a out asymmetricManyToMany `in` b)

        traverse(from = a, path = asymmetricManyToMany, expecting = listOf(b))
    }

    @Test
    fun `test traverse AsymmetricOptionalToMany relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        traverse(from = a, path = asymmetricOptionalToMany, expecting = listOf())

        gm.saveE(a out asymmetricOptionalToMany `in` listOf(b, c))

        traverse(from = a, path = asymmetricOptionalToMany, expecting = listOf(b, c))
        traverse(from = b, path = asymmetricOptionalToMany, expecting = listOf())
        traverse(from = c, path = asymmetricOptionalToMany, expecting = listOf())
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricOptionalToMany relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToMany `in` b)
        gm.saveE(c out asymmetricOptionalToMany `in` b)
    }

    @Test
    fun `test duplicate AsymmetricOptionalToMany relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToMany `in` b)
        gm.saveE(a out asymmetricOptionalToMany `in` b)

        traverse(from = a, path = asymmetricOptionalToMany, expecting = listOf(b))
    }

    @Test
    fun `test traverse AsymmetricManyToOptional relationship`() {
        val asymmetricManyToOptional: Relationship.AsymmetricManyToOptional<VertexWithInt, VertexWithInt> =
                asymmetricOptionalToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        traverse(from = a, path = asymmetricManyToOptional, expecting = null)
        traverse(from = b, path = asymmetricManyToOptional, expecting = null)

        gm.saveE(listOf(a, b) out asymmetricManyToOptional `in` c)

        traverse(from = a, path = asymmetricManyToOptional, expecting = c)
        traverse(from = b, path = asymmetricManyToOptional, expecting = c)
        traverse(from = c, path = asymmetricManyToOptional, expecting = null)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricManyToOptional relationship conflicting edge to vertex`() {
        val asymmetricManyToOptional: Relationship.AsymmetricManyToOptional<VertexWithInt, VertexWithInt> =
                asymmetricOptionalToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricManyToOptional `in` b)
        gm.saveE(a out asymmetricManyToOptional `in` c)
    }

    @Test
    fun `test duplicate AsymmetricManyToOptional relationship is no-op`() {
        val asymmetricManyToOptional: Relationship.AsymmetricManyToOptional<VertexWithInt, VertexWithInt> =
                asymmetricOptionalToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricManyToOptional `in` b)
        gm.saveE(a out asymmetricManyToOptional `in` b)

        traverse(from = a, path = asymmetricManyToOptional, expecting = b)
    }

    @Test
    fun `test traverse AsymmetricSingleToMany relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToMany `in` listOf(b, c))

        traverse(from = a, path = asymmetricSingleToMany, expecting = listOf(b, c))
        traverse(from = b, path = asymmetricSingleToMany, expecting = listOf())
        traverse(from = c, path = asymmetricSingleToMany, expecting = listOf())
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricSingleToMany relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToMany `in` b)
        gm.saveE(c out asymmetricSingleToMany `in` b)
    }

    @Test
    fun `test duplicate AsymmetricSingleToMany relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToMany `in` b)
        gm.saveE(a out asymmetricSingleToMany `in` b)

        traverse(from = a, path = asymmetricSingleToMany, expecting = listOf(b))
    }

    @Test
    fun `test traverse AsymmetricManyToSingle relationship`() {
        val asymmetricManyToSingle: Relationship.AsymmetricManyToSingle<VertexWithInt, VertexWithInt> =
                asymmetricSingleToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(listOf(a, b) out asymmetricManyToSingle `in` c)

        traverse(from = a, path = asymmetricManyToSingle, expecting = c)
        traverse(from = b, path = asymmetricManyToSingle, expecting = c)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricManySingle relationship conflicting edge to vertex`() {
        val asymmetricManyToSingle: Relationship.AsymmetricManyToSingle<VertexWithInt, VertexWithInt> =
                asymmetricSingleToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricManyToSingle `in` b)
        gm.saveE(a out asymmetricManyToSingle `in` c)
    }

    @Test(expected = MissingEdge::class)
    fun `test traverse AsymmetricManySingle relationship missing to vertex`() {
        val asymmetricManyToSingle: Relationship.AsymmetricManyToSingle<VertexWithInt, VertexWithInt> =
                asymmetricSingleToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        gm.traverse(a out asymmetricManyToSingle)
    }

    @Test
    fun `test duplicate AsymmetricManySingle relationship is no-op`() {
        val asymmetricManyToSingle: Relationship.AsymmetricManyToSingle<VertexWithInt, VertexWithInt> =
                asymmetricSingleToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricManyToSingle `in` b)
        gm.saveE(a out asymmetricManyToSingle `in` b)

        traverse(from = a, path = asymmetricManyToSingle, expecting = b)
    }

    @Test
    fun `test traverse AsymmetricSingleToSingle relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToSingle `in` b)
        gm.saveE(b out asymmetricSingleToSingle `in` c)
        gm.saveE(c out asymmetricSingleToSingle `in` a)

        traverse(from = a, path = asymmetricSingleToSingle, expecting = b)
        traverse(from = b, path = asymmetricSingleToSingle, expecting = c)
        traverse(from = c, path = asymmetricSingleToSingle, expecting = a)

        traverse(from = a, path = asymmetricSingleToSingle.inverse, expecting = c)
        traverse(from = b, path = asymmetricSingleToSingle.inverse, expecting = a)
        traverse(from = c, path = asymmetricSingleToSingle.inverse, expecting = b)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricSingleToSingle relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToSingle `in` b)
        gm.saveE(a out asymmetricSingleToSingle `in` c)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricSingleToSingle relationship conflicting edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToSingle `in` b)
        gm.saveE(c out asymmetricSingleToSingle `in` b)
    }

    @Test(expected = MissingEdge::class)
    fun `test traverse AsymmetricSingleToSingle relationship missing edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        gm.traverse(a out asymmetricSingleToSingle)
    }

    @Test
    fun `test duplicate AsymmetricSingleToSingle relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToSingle `in` b)
        gm.saveE(a out asymmetricSingleToSingle `in` b)

        traverse(from = a, path = asymmetricSingleToSingle, expecting = b)
    }

    @Test
    fun `test traverse AsymmetricSingleToOptional relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        traverse(from = a, path = asymmetricSingleToOptional, expecting = null)

        gm.saveE(a out asymmetricSingleToOptional `in` b)

        traverse(from = a, path = asymmetricSingleToOptional, expecting = b)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricSingleToOptional relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToOptional `in` b)
        gm.saveE(a out asymmetricSingleToOptional `in` c)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricSingleToOptional relationship conflicting edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToOptional `in` b)
        gm.saveE(c out asymmetricSingleToOptional `in` b)
    }

    @Test
    fun `test duplicate AsymmetricSingleToOptional relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToOptional `in` b)
        gm.saveE(a out asymmetricSingleToOptional `in` b)

        traverse(from = a, path = asymmetricSingleToOptional, expecting = b)
    }

    @Test
    fun `test traverse AsymmetricOptionalToSingle relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToSingle `in` b)

        traverse(from = a, path = asymmetricOptionalToSingle, expecting = b)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricOptionalToSingle relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToSingle `in` b)
        gm.saveE(a out asymmetricOptionalToSingle `in` c)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricOptionalToSingle relationship conflicting edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToSingle `in` b)
        gm.saveE(c out asymmetricOptionalToSingle `in` b)
    }

    @Test(expected = MissingEdge::class)
    fun `test traverse AsymmetricOptionalToSingle relationship missing edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        gm.traverse(a out asymmetricOptionalToSingle)
    }

    @Test
    fun `test duplicate AsymmetricOptionalToSingle relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToSingle `in` b)
        gm.saveE(a out asymmetricOptionalToSingle `in` b)

        traverse(from = a, path = asymmetricOptionalToSingle, expecting = b)
    }

    @Test
    fun `test traverse AsymmetricOptionalToOptional relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        traverse(from = a, path = asymmetricOptionalToOptional, expecting = null)

        gm.saveE(a out asymmetricOptionalToOptional `in` b)

        traverse(from = a, path = asymmetricOptionalToOptional, expecting = b)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricOptionalToOptional relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToOptional `in` b)
        gm.saveE(a out asymmetricOptionalToOptional `in` c)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save AsymmetricOptionalToOptional relationship conflicting edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToOptional `in` b)
        gm.saveE(c out asymmetricOptionalToOptional `in` b)
    }

    @Test
    fun `test duplicate AsymmetricOptionalToOptional relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToOptional `in` b)
        gm.saveE(a out asymmetricOptionalToOptional `in` b)

        traverse(from = a, path = asymmetricOptionalToOptional, expecting = b)
    }

    @Test
    fun `test traverse SymmetricOptionalToOptional relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        traverse(from = a, path = symmetricOptionalToOptional, expecting = null)
        traverse(from = b, path = symmetricOptionalToOptional, expecting = null)

        traverse(from = a, path = symmetricOptionalToOptional.inverse, expecting = null)
        traverse(from = b, path = symmetricOptionalToOptional.inverse, expecting = null)

        gm.saveE(a out symmetricOptionalToOptional `in` b)

        traverse(from = a, path = symmetricOptionalToOptional, expecting = b)
        traverse(from = b, path = symmetricOptionalToOptional, expecting = a)

        traverse(from = a, path = symmetricOptionalToOptional.inverse, expecting = b)
        traverse(from = b, path = symmetricOptionalToOptional.inverse, expecting = a)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save SymmetricOptionalToOptional relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out symmetricOptionalToOptional `in` b)
        gm.saveE(a out symmetricOptionalToOptional `in` c)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save SymmetricOptionalToOptional relationship conflicting edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out symmetricOptionalToOptional `in` b)
        gm.saveE(c out symmetricOptionalToOptional `in` b)
    }

    @Test
    fun `test duplicate SymmetricOptionalToOptional relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out symmetricOptionalToOptional `in` b)
        gm.saveE(b out symmetricOptionalToOptional `in` a)

        traverse(from = a, path = symmetricOptionalToOptional, expecting = b)
        traverse(from = b, path = symmetricOptionalToOptional, expecting = a)

        traverse(from = a, path = symmetricOptionalToOptional.inverse, expecting = b)
        traverse(from = b, path = symmetricOptionalToOptional.inverse, expecting = a)
    }

    @Test
    fun `test traverse SymmetricSingleToSingle relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out symmetricSingleToSingle `in` b)

        traverse(from = a, path = symmetricSingleToSingle, expecting = b)
        traverse(from = b, path = symmetricSingleToSingle, expecting = a)

        traverse(from = a, path = symmetricSingleToSingle.inverse, expecting = b)
        traverse(from = b, path = symmetricSingleToSingle.inverse, expecting = a)
    }

    @Test
    fun `test duplicate SymmetricSingleToSingle relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out symmetricSingleToSingle `in` b)
        gm.saveE(b out symmetricSingleToSingle `in` a)

        traverse(from = a, path = symmetricSingleToSingle, expecting = b)
        traverse(from = b, path = symmetricSingleToSingle, expecting = a)

        traverse(from = a, path = symmetricSingleToSingle.inverse, expecting = b)
        traverse(from = b, path = symmetricSingleToSingle.inverse, expecting = a)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save SymmetricSingleToSingle relationship conflicting edge from vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out symmetricSingleToSingle `in` b)
        gm.saveE(a out symmetricSingleToSingle `in` c)
    }

    @Test(expected = ConflictingEdge::class)
    fun `test save SymmetricSingleToSingle relationship conflicting edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out symmetricSingleToSingle `in` b)
        gm.saveE(c out symmetricSingleToSingle `in` b)
    }

    @Test(expected = MissingEdge::class)
    fun `test traverse SymmetricSingleToSingle relationship missing edge to vertex`() {
        val a = gm.saveV(VertexWithInt.sample())
        gm.traverse(a out symmetricSingleToSingle)
    }

    @Test
    fun `test traverse SymmetricManyToMany relationship`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out symmetricManyToMany `in` listOf(b, c))
        gm.saveE(b out symmetricManyToMany `in` c)

        traverse(from = a, path = symmetricManyToMany, expecting = listOf(b, c))
        traverse(from = b, path = symmetricManyToMany, expecting = listOf(c, a))
        traverse(from = c, path = symmetricManyToMany, expecting = listOf(a, b))

        traverse(from = a, path = symmetricManyToMany.inverse, expecting = listOf(b, c))
        traverse(from = b, path = symmetricManyToMany.inverse, expecting = listOf(c, a))
        traverse(from = c, path = symmetricManyToMany.inverse, expecting = listOf(a, b))
    }

    @Test
    fun `test duplicate SymmetricManyToMany relationship is no-op`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out symmetricManyToMany `in` b)
        gm.saveE(b out symmetricManyToMany `in` a)

        traverse(from = a, path = symmetricManyToMany, expecting = listOf(b))
        traverse(from = b, path = symmetricManyToMany, expecting = listOf(a))

        traverse(from = a, path = symmetricManyToMany.inverse, expecting = listOf(b))
        traverse(from = b, path = symmetricManyToMany.inverse, expecting = listOf(a))
    }

    private fun <FROM : Any, TO : Any> traverse(from: FROM, path: Path.ToSingle<FROM, TO>, expecting: TO) {
        val traversalResult = gm.traverse(path.from(from))
        assertThat(traversalResult).isEqualTo(expecting)
    }

    private fun <FROM : Any, TO : Any> traverse(from: FROM, path: Path.ToMany<FROM, TO>, expecting: List<TO>) {
        val traversalResult = gm.traverse(path.from(from))
        assertThat(traversalResult).hasSize(expecting.size)
        assertThat(traversalResult).isEqualTo(expecting)
    }

    private fun <FROM : Any, TO : Any> traverse(from: FROM, path: Path.ToOptional<FROM, TO>, expecting: TO?) {
        val traversalResult = gm.traverse(path.from(from))
        if (expecting == null) {
            assertThat(traversalResult).isNull()
        } else {
            assertThat(traversalResult).isEqualTo(expecting)
        }
    }


    @Test
    fun `test traverse ManyToMany to SingleToOptional connection (ManyToMany)`() {
        val manyToManyConnection: Connection.ManyToMany<VertexWithInt, VertexWithInt> = asymmetricManyToMany link asymmetricSingleToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricManyToMany `in` listOf(b, c))
        gm.saveE(b out asymmetricManyToMany `in` c)

        traverse(from = a, path = manyToManyConnection, expecting = listOf())
        traverse(from = b, path = manyToManyConnection, expecting = listOf())
        traverse(from = c, path = manyToManyConnection, expecting = listOf())
        traverse(from = x, path = manyToManyConnection, expecting = listOf())

        gm.saveE(b out asymmetricSingleToOptional `in` x)

        traverse(from = a, path = manyToManyConnection, expecting = listOf(x))
        traverse(from = b, path = manyToManyConnection, expecting = listOf())
        traverse(from = c, path = manyToManyConnection, expecting = listOf())
        traverse(from = x, path = manyToManyConnection, expecting = listOf())
    }

    @Test
    fun `test traverse OptionalToMany to OptionalToMany connection (ManyToMany)`() {
        val manyToManyConnection: Connection.ManyToMany<VertexWithInt, VertexWithInt> = asymmetricOptionalToMany.inverse link asymmetricOptionalToMany
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(listOf(a, b) out asymmetricOptionalToMany.inverse `in` c)

        traverse(from = a, path = manyToManyConnection, expecting = listOf(a, b))
        traverse(from = b, path = manyToManyConnection, expecting = listOf(a, b))
        traverse(from = c, path = manyToManyConnection, expecting = listOf())
        traverse(from = x, path = manyToManyConnection, expecting = listOf())
        traverse(from = y, path = manyToManyConnection, expecting = listOf())

        gm.saveE(c out asymmetricOptionalToMany `in` listOf(x, y))

        traverse(from = a, path = manyToManyConnection, expecting = listOf(x, y, a, b))
        traverse(from = b, path = manyToManyConnection, expecting = listOf(x, y, a, b))
        traverse(from = c, path = manyToManyConnection, expecting = listOf())
        traverse(from = x, path = manyToManyConnection, expecting = listOf(x, y, a, b))
        traverse(from = y, path = manyToManyConnection, expecting = listOf(x, y, a, b))
    }

    @Test
    fun `test traverse OptionalToOptional to ManyToMany connection (ManyToMany)`() {
        val manyToManyConnection: Connection.ManyToMany<VertexWithInt, VertexWithInt> = asymmetricOptionalToOptional link asymmetricManyToMany
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToOptional `in` b)

        traverse(from = a, path = manyToManyConnection, expecting = listOf())
        traverse(from = b, path = manyToManyConnection, expecting = listOf())
        traverse(from = x, path = manyToManyConnection, expecting = listOf())
        traverse(from = y, path = manyToManyConnection, expecting = listOf())

        gm.saveE(b out asymmetricManyToMany `in` listOf(x, y))

        traverse(from = a, path = manyToManyConnection, expecting = listOf(x, y))
        traverse(from = b, path = manyToManyConnection, expecting = listOf())
        traverse(from = x, path = manyToManyConnection, expecting = listOf())
        traverse(from = y, path = manyToManyConnection, expecting = listOf())
    }

    @Test
    fun `test traverse ManyToMany to ManyToMany connection (ManyToMany)`() {
        val manyToManyConnection: Connection.ManyToMany<VertexWithInt, VertexWithInt> = asymmetricManyToMany link asymmetricManyToMany
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricManyToMany `in` listOf(b, c))

        traverse(from = a, path = manyToManyConnection, expecting = listOf())
        traverse(from = b, path = manyToManyConnection, expecting = listOf())
        traverse(from = c, path = manyToManyConnection, expecting = listOf())
        traverse(from = x, path = manyToManyConnection, expecting = listOf())
        traverse(from = y, path = manyToManyConnection, expecting = listOf())

        gm.saveE(b out asymmetricManyToMany `in` c)

        traverse(from = a, path = manyToManyConnection, expecting = listOf(c))
        traverse(from = b, path = manyToManyConnection, expecting = listOf())
        traverse(from = c, path = manyToManyConnection, expecting = listOf())
        traverse(from = x, path = manyToManyConnection, expecting = listOf())
        traverse(from = y, path = manyToManyConnection, expecting = listOf())

        gm.saveE(b out asymmetricManyToMany `in` listOf(x, y))

        traverse(from = a, path = manyToManyConnection, expecting = listOf(c, x, y))
        traverse(from = b, path = manyToManyConnection, expecting = listOf())
        traverse(from = c, path = manyToManyConnection, expecting = listOf())
        traverse(from = x, path = manyToManyConnection, expecting = listOf())
        traverse(from = y, path = manyToManyConnection, expecting = listOf())

        gm.saveE(c out asymmetricManyToMany `in` y)

        traverse(from = a, path = manyToManyConnection, expecting = listOf(c, x, y, y))
        traverse(from = b, path = manyToManyConnection, expecting = listOf(y))
        traverse(from = c, path = manyToManyConnection, expecting = listOf())
        traverse(from = x, path = manyToManyConnection, expecting = listOf())
        traverse(from = y, path = manyToManyConnection, expecting = listOf())
    }

    @Test
    fun `test traverse ManyToOptional to OptionalToOptional connection (ManyToOptional)`() {
        val manyToOptionalConnection: Connection.ManyToOptional<VertexWithInt, VertexWithInt> = asymmetricOptionalToMany.inverse link asymmetricOptionalToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(listOf(a, b) out asymmetricOptionalToMany.inverse `in` c)

        traverse(from = a, path = manyToOptionalConnection, expecting = null)
        traverse(from = b, path = manyToOptionalConnection, expecting = null)
        traverse(from = c, path = manyToOptionalConnection, expecting = null)
        traverse(from = x, path = manyToOptionalConnection, expecting = null)

        gm.saveE(c out asymmetricOptionalToOptional `in` x)

        traverse(from = a, path = manyToOptionalConnection, expecting = x)
        traverse(from = b, path = manyToOptionalConnection, expecting = x)
        traverse(from = c, path = manyToOptionalConnection, expecting = null)
        traverse(from = x, path = manyToOptionalConnection, expecting = null)
    }

    @Test
    fun `test traverse ManyToSingle to SingleToOptional connection (ManyToOptional)`() {
        val manyToOptionalConnection: Connection.ManyToOptional<VertexWithInt, VertexWithInt> = asymmetricSingleToMany.inverse link asymmetricSingleToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(listOf(a, b) out asymmetricSingleToMany.inverse `in` c)

        traverse(from = a, path = manyToOptionalConnection, expecting = null)
        traverse(from = b, path = manyToOptionalConnection, expecting = null)

        gm.saveE(c out asymmetricSingleToOptional `in` x)

        traverse(from = a, path = manyToOptionalConnection, expecting = x)
        traverse(from = b, path = manyToOptionalConnection, expecting = x)
    }

    @Test
    fun `test traverse SingleToSingle to ManyToOptional connection (ManyToOptional)`() {
        val manyToOptionalConnection: Connection.ManyToOptional<VertexWithInt, VertexWithInt> = asymmetricSingleToSingle link asymmetricOptionalToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToSingle `in` b)
        gm.saveE(b out asymmetricSingleToSingle `in` c)
        gm.saveE(c out asymmetricSingleToSingle `in` a)

        traverse(from = a, path = manyToOptionalConnection, expecting = null)
        traverse(from = b, path = manyToOptionalConnection, expecting = null)
        traverse(from = c, path = manyToOptionalConnection, expecting = null)

        gm.saveE(listOf(a, b) out asymmetricOptionalToMany.inverse `in` x)

        traverse(from = a, path = manyToOptionalConnection, expecting = x)
        traverse(from = b, path = manyToOptionalConnection, expecting = null)
        traverse(from = c, path = manyToOptionalConnection, expecting = x)
    }

    @Test
    fun `test traverse ManyToSingle to SingleToSingle connection (ManyToSingle)`() {
        val manyToSingleConnection: Connection.ManyToSingle<VertexWithInt, VertexWithInt> = asymmetricSingleToMany.inverse link symmetricSingleToSingle
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(listOf(a, b) out asymmetricSingleToMany.inverse `in` c)
        gm.saveE(c out symmetricSingleToSingle `in` x)

        traverse(from = a, path = manyToSingleConnection, expecting = x)
        traverse(from = b, path = manyToSingleConnection, expecting = x)
    }

    @Test
    fun `test traverse OptionalToSingle to ManyToSingle connection (ManyToSingle)`() {
        val manyToSingleConnection: Connection.ManyToSingle<VertexWithInt, VertexWithInt> = asymmetricOptionalToSingle link asymmetricSingleToMany.inverse
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val d = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToSingle `in` b)
        gm.saveE(c out asymmetricOptionalToSingle `in` d)
        gm.saveE(listOf(b, d) out  asymmetricSingleToMany.inverse `in` x)

        traverse(from = a, path = manyToSingleConnection, expecting = x)
        traverse(from = c, path = manyToSingleConnection, expecting = x)
    }

    @Test
    fun `test traverse OptionalToMany to SingleToSingle connection (OptionalToMany)`() {
        val optionalToManyConnection: Connection.OptionalToMany<VertexWithInt, VertexWithInt> = asymmetricOptionalToMany link asymmetricSingleToSingle
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToMany `in` listOf(b, c))

        traverse(from = b, path = optionalToManyConnection, expecting = listOf())
        traverse(from = x, path = optionalToManyConnection, expecting = listOf())
        traverse(from = y, path = optionalToManyConnection, expecting = listOf())

        gm.saveE(b out asymmetricSingleToSingle `in` x)
        gm.saveE(c out asymmetricSingleToSingle `in` y)

        traverse(from = a, path = optionalToManyConnection, expecting = listOf(x, y))
        traverse(from = b, path = optionalToManyConnection, expecting = listOf())
        traverse(from = x, path = optionalToManyConnection, expecting = listOf())
        traverse(from = y, path = optionalToManyConnection, expecting = listOf())
    }

    @Test
    fun `test traverse SingleToSingle to OptionalToMany connection (OptionalToMany)`() {
        val optionalToManyConnection: Connection.OptionalToMany<VertexWithInt, VertexWithInt> = asymmetricSingleToSingle link asymmetricOptionalToMany
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToSingle `in` b)
        gm.saveE(b out asymmetricSingleToSingle `in` c)
        gm.saveE(c out asymmetricSingleToSingle `in` a)

        traverse(from = a, path = optionalToManyConnection, expecting = listOf())
        traverse(from = b, path = optionalToManyConnection, expecting = listOf())
        traverse(from = c, path = optionalToManyConnection, expecting = listOf())

        gm.saveE(a out asymmetricOptionalToMany `in` listOf(x, y))

        traverse(from = a, path = optionalToManyConnection, expecting = listOf())
        traverse(from = b, path = optionalToManyConnection, expecting = listOf())
        traverse(from = c, path = optionalToManyConnection, expecting = listOf(x, y))
    }

    @Test
    fun `test traverse OptionalToSingle to SingleToMany connection (OptionalToMany)`() {
        val optionalToManyConnection: Connection.OptionalToMany<VertexWithInt, VertexWithInt> = asymmetricOptionalToSingle link asymmetricSingleToMany
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToSingle `in` b)

        traverse(from = a, path = optionalToManyConnection, expecting = listOf())

        gm.saveE(b out asymmetricSingleToMany `in` listOf(x, y))

        traverse(from = a, path = optionalToManyConnection, expecting = listOf(x, y))
    }

    @Test
    fun `test traverse SingleToOptional to SingleToMany connection (SingleToMany)`() {
        val singleToManyConnection: Connection.SingleToMany<VertexWithInt, VertexWithInt> = asymmetricSingleToOptional link asymmetricSingleToMany
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToOptional `in` b)

        traverse(from = a, path = singleToManyConnection, expecting = listOf())
        traverse(from = b, path = singleToManyConnection, expecting = listOf())
        traverse(from = x, path = singleToManyConnection, expecting = listOf())
        traverse(from = y, path = singleToManyConnection, expecting = listOf())

        gm.saveE(b out asymmetricSingleToMany `in` listOf(x, y))

        traverse(from = a, path = singleToManyConnection, expecting = listOf(x, y))
        traverse(from = b, path = singleToManyConnection, expecting = listOf())
        traverse(from = x, path = singleToManyConnection, expecting = listOf())
        traverse(from = y, path = singleToManyConnection, expecting = listOf())
    }

    @Test
    fun `test traverse SingleToMany to SingleToOptional connection (SingleToMany)`() {
        val singleToManyConnection: Connection.SingleToMany<VertexWithInt, VertexWithInt> = asymmetricSingleToMany link asymmetricSingleToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToMany `in` listOf(b, c))

        traverse(from = a, path = singleToManyConnection, expecting = listOf())
        traverse(from = b, path = singleToManyConnection, expecting = listOf())
        traverse(from = c, path = singleToManyConnection, expecting = listOf())
        traverse(from = x, path = singleToManyConnection, expecting = listOf())
        traverse(from = y, path = singleToManyConnection, expecting = listOf())

        gm.saveE(b out asymmetricSingleToOptional `in` x)
        gm.saveE(c out asymmetricSingleToOptional `in` y)

        traverse(from = a, path = singleToManyConnection, expecting = listOf(x, y))
        traverse(from = b, path = singleToManyConnection, expecting = listOf())
        traverse(from = c, path = singleToManyConnection, expecting = listOf())
        traverse(from = x, path = singleToManyConnection, expecting = listOf())
        traverse(from = y, path = singleToManyConnection, expecting = listOf())
    }

    @Test
    fun `test traverse SingleToSingle to SingleToSingle connection (SingleToSingle)`() {
        val singleToSingleConnection: Connection.SingleToSingle<VertexWithInt, VertexWithInt> = asymmetricSingleToSingle link asymmetricSingleToSingle
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val d = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToSingle `in` b)
        gm.saveE(b out asymmetricSingleToSingle `in` c)
        gm.saveE(c out asymmetricSingleToSingle `in` d)
        gm.saveE(d out asymmetricSingleToSingle `in` a)

        traverse(from = a, path = singleToSingleConnection, expecting = c)
        traverse(from = b, path = singleToSingleConnection, expecting = d)
        traverse(from = c, path = singleToSingleConnection, expecting = a)
    }

    @Test
    fun `test traverse SingleToOptional to SingleToOptional connection (SingleToOptional)`() {
        val singleToOptionalConnection: Connection.SingleToOptional<VertexWithInt, VertexWithInt> = asymmetricSingleToOptional link asymmetricSingleToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToOptional `in` b)

        traverse(from = a, path = singleToOptionalConnection, expecting = null)
        traverse(from = b, path = singleToOptionalConnection, expecting = null)
        traverse(from = c, path = singleToOptionalConnection, expecting = null)
        traverse(from = x, path = singleToOptionalConnection, expecting = null)

        gm.saveE(b out asymmetricSingleToOptional `in` c)

        traverse(from = a, path = singleToOptionalConnection, expecting = c)
        traverse(from = b, path = singleToOptionalConnection, expecting = null)
        traverse(from = c, path = singleToOptionalConnection, expecting = null)
        traverse(from = x, path = singleToOptionalConnection, expecting = null)

        gm.saveE(c out asymmetricSingleToOptional `in` x)

        traverse(from = a, path = singleToOptionalConnection, expecting = c)
        traverse(from = b, path = singleToOptionalConnection, expecting = x)
        traverse(from = c, path = singleToOptionalConnection, expecting = null)
        traverse(from = x, path = singleToOptionalConnection, expecting = null)
    }

    @Test
    fun `test traverse SingleToSingle to SingleToOptional connection (SingleToOptional)`() {
        val singleToOptionalConnection: Connection.SingleToOptional<VertexWithInt, VertexWithInt> = asymmetricSingleToSingle link asymmetricSingleToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToSingle `in` b)
        gm.saveE(b out asymmetricSingleToSingle `in` c)
        gm.saveE(c out asymmetricSingleToSingle `in` a)

        traverse(from = a, path = singleToOptionalConnection, expecting = null)
        traverse(from = b, path = singleToOptionalConnection, expecting = null)
        traverse(from = c, path = singleToOptionalConnection, expecting = null)

        gm.saveE(a out asymmetricSingleToOptional `in` x)

        traverse(from = a, path = singleToOptionalConnection, expecting = null)
        traverse(from = b, path = singleToOptionalConnection, expecting = null)
        traverse(from = c, path = singleToOptionalConnection, expecting = x)
    }

    @Test
    fun `test traverse SingleToSingle to OptionalToSingle connection (OptionalToSingle)`() {
        val optionalToSingleConnection: Connection.OptionalToSingle<VertexWithInt, VertexWithInt> = asymmetricSingleToSingle link asymmetricOptionalToSingle
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToSingle `in` b)
        gm.saveE(b out asymmetricSingleToSingle `in` c)
        gm.saveE(c out asymmetricSingleToSingle `in` a)
        gm.saveE(c out asymmetricOptionalToSingle `in` x)

        traverse(from = b, path = optionalToSingleConnection, expecting = x)
    }

    @Test
    fun `test traverse OptionalToSingle to SingleToSingle connection (OptionalToSingle)`() {
        val optionalToSingleConnection: Connection.OptionalToSingle<VertexWithInt, VertexWithInt> = asymmetricOptionalToSingle link symmetricSingleToSingle
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToSingle `in` b)
        gm.saveE(b out symmetricSingleToSingle `in` x)

        traverse(from = a, path = optionalToSingleConnection, expecting = x)
    }

    @Test
    fun `test traverse OptionalToSingle to SingleToOptional connection (OptionalToOptional)`() {
        val optionalToOptionalConnection: Connection.OptionalToOptional<VertexWithInt, VertexWithInt> = asymmetricOptionalToSingle link asymmetricSingleToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricOptionalToSingle `in` b)
        gm.saveE(b out asymmetricOptionalToSingle `in` c)

        traverse(from = a, path = optionalToOptionalConnection, expecting = null)
        traverse(from = b, path = optionalToOptionalConnection, expecting = null)

        gm.saveE(c out asymmetricSingleToOptional `in` x)

        traverse(from = a, path = optionalToOptionalConnection, expecting = null)
        traverse(from = b, path = optionalToOptionalConnection, expecting = x)
    }

    @Test
    fun `test traverse OptionalToOptional to OptionalToSingle connection (OptionalToOptional)`() {
        val optionalToOptionalConnection: Connection.OptionalToOptional<VertexWithInt, VertexWithInt> = symmetricOptionalToOptional link asymmetricOptionalToSingle

        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())
        val y = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out symmetricOptionalToOptional `in` b)

        traverse(from = x, path = optionalToOptionalConnection, expecting = null)
        traverse(from = y, path = optionalToOptionalConnection, expecting = null)

        gm.saveE(a out asymmetricOptionalToSingle `in` x)
        gm.saveE(b out asymmetricOptionalToSingle `in` y)

        traverse(from = a, path = optionalToOptionalConnection, expecting = y)
        traverse(from = b, path = optionalToOptionalConnection, expecting = x)
        traverse(from = x, path = optionalToOptionalConnection, expecting = null)
        traverse(from = y, path = optionalToOptionalConnection, expecting = null)
    }

    @Test
    fun `test traverse SingleToOptional to OptionalToOptional connection (OptionalToOptional)`() {
        val optionalToOptionalConnection: Connection.OptionalToOptional<VertexWithInt, VertexWithInt> = asymmetricSingleToOptional link asymmetricOptionalToOptional
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val x = gm.saveV(VertexWithInt.sample())

        gm.saveE(a out asymmetricSingleToOptional `in` b)
        gm.saveE(c out asymmetricSingleToOptional `in` a)

        traverse(from = a, path = optionalToOptionalConnection, expecting = null)
        traverse(from = b, path = optionalToOptionalConnection, expecting = null)
        traverse(from = c, path = optionalToOptionalConnection, expecting = null)
        traverse(from = x, path = optionalToOptionalConnection, expecting = null)

        gm.saveE(b out asymmetricOptionalToOptional `in` x)

        traverse(from = a, path = optionalToOptionalConnection, expecting = x)
        traverse(from = b, path = optionalToOptionalConnection, expecting = null)
        traverse(from = c, path = optionalToOptionalConnection, expecting = null)
        traverse(from = x, path = optionalToOptionalConnection, expecting = null)
    }

    @Test
    fun `test traverse step with map`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        gm.saveE(a out asymmetricSingleToSingle `in` b)
        val traversalWithMap = asymmetricSingleToSingle.map { vertexWithInt ->
            vertexWithInt.a
        }
        traverse(from = a, path = traversalWithMap, expecting = b.a)
    }

    @Test
    fun `test traverse step with flatmap`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        gm.saveE(a out asymmetricSingleToMany `in` b)
        gm.saveE(a out asymmetricSingleToMany `in` c)
        val traversalWithFlatMap = asymmetricSingleToMany.flatMap { vertexWithInt ->
            listOf(vertexWithInt.a, vertexWithInt.a * 10)
        }
        traverse(from = a, path = traversalWithFlatMap, expecting = listOf(b.a, b.a * 10, c.a, c.a * 10))
    }

    @Test
    fun `test traverse step with filter`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        gm.saveE(a out asymmetricSingleToSingle `in` b)

        val traversalWithFilterAll = asymmetricSingleToSingle.filter { _ ->
            false
        }
        traverse(from = a, path = traversalWithFilterAll, expecting = null)

        val traversalWithFilterNone = asymmetricSingleToSingle.filter { _ ->
            true
        }
        traverse(from = a, path = traversalWithFilterNone, expecting = b)
    }

    @Test
    fun `test traverse step with filterMap`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        gm.saveE(a out asymmetricSingleToSingle `in` b)
        val traversalWithEven = asymmetricSingleToSingle.filterMap { vertexWithInt ->
            if (vertexWithInt.a % 2 == 0) vertexWithInt.a else null
        }
        traverse(from = a, path = traversalWithEven, expecting = if (b.a % 2 == 0) b.a else null)
    }

    @Test
    fun `test traverse step with slice`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val d = gm.saveV(VertexWithInt.sample())
        val e = gm.saveV(VertexWithInt.sample())
        gm.saveE(a out asymmetricSingleToMany `in` listOf(b, c, d, e))
        val traversalWithSlice = asymmetricSingleToMany.slice(range = LongRange(1, 2))
        traverse(from = a, path = traversalWithSlice, expecting = listOf(c, d))
    }

    @Test
    fun `test traverse step with sort`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt(int = 5))
        val c = gm.saveV(VertexWithInt(int = 4))
        val d = gm.saveV(VertexWithInt(int = 3))
        val e = gm.saveV(VertexWithInt(int = 2))
        gm.saveE(a out asymmetricSingleToMany `in` listOf(b, c, d, e))
        val traversalWithSort = asymmetricSingleToMany.sort(Comparator.comparingInt { it.a })
        traverse(from = a, path = traversalWithSort, expecting = listOf(e, d, c, b))
    }

    @Test
    fun `test traverse step with dedup`() {
        val a = gm.saveV(VertexWithInt.sample())
        val b = gm.saveV(VertexWithInt.sample())
        val c = gm.saveV(VertexWithInt.sample())
        val d = gm.saveV(VertexWithInt.sample())
        gm.saveE(a out asymmetricSingleToMany `in` listOf(b, c))
        gm.saveE(b out asymmetricManyToMany.inverse `in` d)
        gm.saveE(c out asymmetricManyToMany.inverse `in` d)
        val connection = asymmetricSingleToMany link asymmetricManyToMany.inverse
        traverse(from = a, path = connection, expecting = listOf(d, d))
        traverse(from = a, path = connection.dedup(), expecting = listOf(d))
        traverse(from = a, path = connection.map { it.a }.dedup(), expecting = listOf(d.a))
    }
}
