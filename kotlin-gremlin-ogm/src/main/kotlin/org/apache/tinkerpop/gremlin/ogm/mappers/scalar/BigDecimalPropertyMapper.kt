package org.apache.tinkerpop.gremlin.ogm.mappers.scalar

import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import java.math.BigDecimal

internal object BigDecimalPropertyMapper : PropertyBiMapper<BigDecimal, String> {

    override fun forwardMap(from: BigDecimal) = from.toString()

    override fun inverseMap(from: String): BigDecimal = BigDecimal(from)

    override val serializedClass get() = String::class
}
