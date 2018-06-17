package starwars.models

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import java.time.Instant

internal abstract class BaseVertex(
        val id: Long?,
        val createdAt: Instant
) {

    override fun hashCode(): Int = id?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?): Boolean = id != null && other != null && other is BaseVertex && id == other.id
}
