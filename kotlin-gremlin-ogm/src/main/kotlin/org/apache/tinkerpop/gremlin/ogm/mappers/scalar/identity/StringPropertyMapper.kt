package org.apache.tinkerpop.gremlin.ogm.mappers.scalar.identity

internal object StringPropertyMapper : IdentityPropertyMapper<String> {
    override val serializedClass get() = String::class
}
