package org.apache.tinkerpop.gremlin.ogm.mappers.scalar

import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import java.net.URL

internal object URLPropertyMapper : PropertyBiMapper<URL, String> {

    override fun forwardMap(from: URL) = from.toString()

    override fun inverseMap(from: String): URL = URL(from)

    override val serializedClass get() = String::class
}
