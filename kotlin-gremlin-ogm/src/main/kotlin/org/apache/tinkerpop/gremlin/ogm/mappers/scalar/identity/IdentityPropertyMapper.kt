package org.apache.tinkerpop.gremlin.ogm.mappers.scalar.identity

import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper

internal interface IdentityPropertyMapper<TYPE : Any> : PropertyBiMapper<TYPE, TYPE> {
    override fun forwardMap(from: TYPE): TYPE = from
    override fun inverseMap(from: TYPE): TYPE = from
}
