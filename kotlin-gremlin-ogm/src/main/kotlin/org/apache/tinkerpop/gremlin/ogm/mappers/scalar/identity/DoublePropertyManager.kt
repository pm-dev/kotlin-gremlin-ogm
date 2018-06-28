package org.apache.tinkerpop.gremlin.ogm.mappers.scalar.identity

internal object DoublePropertyManager : IdentityPropertyMapper<Double> {
    override val serializedClass get() = Double::class
}
