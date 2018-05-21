package org.apache.tinkerpop.gremlin.ogm.mappers

/**
 * A function that is also able to map elements in its co-domain to its domain
 */
interface BiMapper<X : Any?, Y : Any?> {

    /**
     * Maps an object of type X to an object of type Y. It is expected that the value
     * returned by this function could be passed to [inverseMap] and an object equal to
     * 'from' would be returned.
     */
    fun forwardMap(from: X): Y

    /**
     * Maps an object of type Y to an object of type X. It is expected that the value
     * returned by this function could be passed to [forwardMap] and an object equal to
     * 'from' would be returned.
     */
    fun inverseMap(from: Y): X
}
