package org.apache.tinkerpop.gremlin.ogm.mappers.scalar

import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import java.util.*

internal object UUIDPropertyMapper : PropertyBiMapper<UUID, String> {

    override fun forwardMap(from: UUID) = from.toString()

    override fun inverseMap(from: String): UUID = UUID.fromString(from)

    override val serializedClass get() = String::class
}
