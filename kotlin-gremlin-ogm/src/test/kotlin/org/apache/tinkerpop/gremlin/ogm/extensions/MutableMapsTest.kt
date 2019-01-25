package org.apache.tinkerpop.gremlin.ogm.extensions

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class MutableMapsTest {

    @Test
    fun `test mapValuesInPlace`() {
        val mutableMap = mutableMapOf(
                "first" to 1,
                "second" to 2,
                "third" to 3)
        mutableMap.mapValuesInPlace { it.value * 10 }
        assertThat(mutableMap).hasSize(3)
        assertThat(mutableMap["first"]).isEqualTo(10)
        assertThat(mutableMap["second"]).isEqualTo(20)
        assertThat(mutableMap["third"]).isEqualTo(30)
    }

    @Test
    fun `test toMultiMap`() {
        val multiMap = listOf(1 to "a", 1 to "b", 2 to "c", 2 to "d", 3 to "e", 4 to "f", 5 to "g").iterator().toMultiMap()
        assertThat(multiMap).hasSize(5)
        assertThat(multiMap[1]).hasSize(2)
        assertThat(multiMap[2]).hasSize(2)
        assertThat(multiMap[3]).hasSize(1)
        assertThat(multiMap[4]).hasSize(1)
        assertThat(multiMap[5]).hasSize(1)
    }

    @Test
    fun `test toOptionalMap`() {
        val optionalMap = listOf(1 to "a", 2 to null).iterator().toOptionalMap()
        assertThat(optionalMap).hasSize(2)
        assertThat(optionalMap[1]).isEqualTo("a")
        assertThat(optionalMap[2]).isNull()
    }

    @Test
    fun `test toSingleMap`() {
        val multiMap = listOf(1 to "a", 2 to "b").iterator().toSingleMap()
        assertThat(multiMap).hasSize(2)
        assertThat(multiMap[1]).isEqualTo("a")
        assertThat(multiMap[2]).isEqualTo("b")
    }

    @Test(expected = NoSuchElementException::class)
    fun `test toSingleMap no element`() {
        listOf(1 to "a").iterator().toSingleMap(requireKeys = setOf(1, 2))
    }
}
