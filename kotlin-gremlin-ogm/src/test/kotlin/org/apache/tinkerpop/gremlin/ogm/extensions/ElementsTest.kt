package org.apache.tinkerpop.gremlin.ogm.extensions

import org.apache.tinkerpop.gremlin.ogm.mappers.SerializedProperty
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

internal class ElementsTest {

    private lateinit var vertex: Vertex
    private lateinit var vertexProperties: List<Pair<String, SerializedProperty>>
    private lateinit var serializedProperties: Map<String, SerializedProperty>

    @Before
    fun setup() {
        vertex = TinkerGraph.open().traversal().addV().next()
        serializedProperties = mapOf(
                "names" to listOf(
                        mapOf(
                                "salutations" to emptyList<Any>(),
                                "first" to "Cassius",
                                "last" to "Clay"),
                        mapOf(
                                "salutations" to listOf("Mr.", "Your Greatness"),
                                "first" to "Muhammad",
                                "last" to "Ali")),
                "gender" to "male",
                "fliesLike" to listOf("butterfly"))
        
        vertexProperties = listOf(
                "names.0.salutations" to emptyListToken,
                "names.0.first" to "Cassius",
                "names.0.last" to "Clay",
                "names.1.salutations.0" to "Mr.",
                "names.1.salutations.1" to "Your Greatness",
                "names.1.first" to "Muhammad",
                "names.1.last" to "Ali",
                "gender" to "male",
                "fliesLike.0" to "butterfly")
    }

    @Test
    fun `test set properties`() {
        vertex.setProperties(serializedProperties)
        assertThat(vertex.getTestProperties()).containsAll(vertexProperties)
    }

    @Test
    fun `test get properties`() {
        vertex.applyTestProperties()
        assertThat(vertex.getProperties()).isEqualTo(serializedProperties)
    }

    private fun Vertex.applyTestProperties() =
            vertexProperties.forEach { property(it.first, it.second) }

    private fun Vertex.getTestProperties(): List<Pair<String, SerializedProperty>> =
            properties<Any>().asSequence().toList().map { it.key() to it.value() }
}
