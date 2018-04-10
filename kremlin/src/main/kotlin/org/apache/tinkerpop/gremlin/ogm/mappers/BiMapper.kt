package org.apache.tinkerpop.gremlin.ogm.mappers

/**
 * A function that is also able to map elements in its co-domain to its domain
 */
interface BiMapper<X : Any?, Y : Any?> {
    fun forwardMap(from: X): Y
    fun inverseMap(from: Y): X
}
