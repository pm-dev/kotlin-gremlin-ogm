package org.apache.tinkerpop.gremlin.ogm.mappers.scalar.identity

internal object FloatPropertyMapper : IdentityPropertyMapper<Float> {
    override val serializedClass get() = Float::class
}
