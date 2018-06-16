package starwars.models

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.janusgraph.graphdb.relations.RelationIdentifier

internal abstract class BaseEdge<FROM : Vertex, TO : Vertex>(

        private val id: RelationIdentifier?,

        override val from: FROM,

        override val to: TO

) : Edge<FROM, TO> {

    override fun hashCode(): Int = id?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?): Boolean = id != null && other != null && other is BaseEdge<*, *> && id == other.id
}
