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
        val multiMap = listOf(1, 1, 2, 2, 3, 4, 5).iterator().toMultiMap {
            it.toString() to it
        }
        assertThat(multiMap).hasSize(5)
        assertThat(multiMap["1"]).hasSize(2)
        assertThat(multiMap["2"]).hasSize(2)
        assertThat(multiMap["3"]).hasSize(1)
        assertThat(multiMap["4"]).hasSize(1)
        assertThat(multiMap["5"]).hasSize(1)
    }
}
