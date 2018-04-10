package org.apache.tinkerpop.gremlin.ogm.mappers

/**
 * An interface marking an object that can be invoked to map an object X from
 * its domain to an object Y from its co-domain
 */
interface Mapper<in X : Any?, out Y : Any?> {
    operator fun invoke(from: X): Y
}
