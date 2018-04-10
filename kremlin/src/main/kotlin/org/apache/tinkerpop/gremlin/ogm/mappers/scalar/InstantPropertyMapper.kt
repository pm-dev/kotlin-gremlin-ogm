package org.apache.tinkerpop.gremlin.ogm.mappers.scalar

import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import java.time.Instant

internal object InstantPropertyMapper : PropertyBiMapper<Instant, String> {

    override fun forwardMap(from: Instant) = "${from.epochSecond}:${from.nano}"

    override fun inverseMap(from: String): Instant {
        val parts = from.split(':')
        val epochSecond = parts[0].toLong()
        val nano = parts[1].toLong()
        return Instant.ofEpochSecond(epochSecond, nano)
    }
}
