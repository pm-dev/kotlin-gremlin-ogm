package org.apache.tinkerpop.gremlin.ogm.mappers.scalar.identity

internal object LongPropertyMapper : IdentityPropertyMapper<Long> {
    override val serializedClass get() = Long::class
}
