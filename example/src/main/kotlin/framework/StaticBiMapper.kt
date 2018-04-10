package framework

import com.google.common.collect.BiMap
import org.apache.tinkerpop.gremlin.ogm.mappers.BiMapper


interface StaticBiMapper<X : Any, Y : Any> : BiMapper<X, Y> {

    val map: BiMap<X, Y>

    override fun forwardMap(from: X): Y = map[from] ?: throw MissingFromDomain(from)
    override fun inverseMap(from: Y): X = map.inverse()[from] ?: throw MissingFromCodomain(from)

    private class MissingFromDomain(obj: Any) : RuntimeException("Value missing from domain of static bi-map: $obj")
    private class MissingFromCodomain(obj: Any) : RuntimeException("Value missing from co-domain of static bi-map: $obj")
}
