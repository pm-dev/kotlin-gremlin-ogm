package starwars.models

import org.apache.tinkerpop.gremlin.ogm.annotations.ID
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.BaseEdge
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import org.janusgraph.graphdb.relations.RelationIdentifier

internal abstract class Edge<OUT : Any, IN : Any>(

        @property:ID
        val id: RelationIdentifier?,

        outV: OUT,

        inV: IN,

        relationship: Relationship<OUT, IN>

) : BaseEdge<OUT, IN>(outV, inV, relationship) {

    override fun hashCode(): Int = id?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?): Boolean = id != null && other != null && other is Edge<*, *> && id == other.id
}
