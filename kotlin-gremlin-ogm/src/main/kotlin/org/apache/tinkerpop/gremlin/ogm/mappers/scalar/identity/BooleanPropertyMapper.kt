package org.apache.tinkerpop.gremlin.ogm.mappers.scalar.identity

internal object BooleanPropertyMapper : IdentityPropertyMapper<Boolean> {
    override val serializedClass get() = Boolean::class
}
