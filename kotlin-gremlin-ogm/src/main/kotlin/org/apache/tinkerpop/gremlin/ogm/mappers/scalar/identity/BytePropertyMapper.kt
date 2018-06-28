package org.apache.tinkerpop.gremlin.ogm.mappers.scalar.identity

internal object BytePropertyMapper : IdentityPropertyMapper<Byte> {
    override val serializedClass get() = Byte::class
}
