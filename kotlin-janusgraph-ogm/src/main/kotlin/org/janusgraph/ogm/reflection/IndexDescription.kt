package org.janusgraph.ogm.reflection

import org.apache.tinkerpop.gremlin.structure.Element
import kotlin.reflect.KClass

internal data class IndexDescription(
        val propertyName: String,
        val dataType: KClass<*>,
        val unique: Boolean,
        val elementType: KClass<out Element>,
        val elementLabel: String) {

    val indexName get() = "$elementLabel-$propertyName${if (unique) "-unique" else ""}-index"
}
