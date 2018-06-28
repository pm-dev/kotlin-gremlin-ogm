package org.apache.tinkerpop.gremlin.ogm.mappers.scalar.identity

internal object IntegerPropertyMapper : IdentityPropertyMapper<Int> {
    override val serializedClass get() = Int::class
}
