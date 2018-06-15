package starwars.models

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.BaseEdge
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import org.janusgraph.graphdb.relations.RelationIdentifier

internal abstract class Edge<FROM : Any, TO : Any>(

        private val id: RelationIdentifier?,

        from: FROM,

        to: TO,

        relationship: Relationship<FROM, TO>

) : BaseEdge<FROM, TO>(from, to, relationship) {

    override fun hashCode(): Int = id?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?): Boolean = id != null && other != null && other is Edge<*, *> && id == other.id
}
