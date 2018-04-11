package starwars.models

import org.apache.tinkerpop.gremlin.ogm.annotations.ID
import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import java.time.Instant

internal abstract class Node(

        @property:ID
        val id: Long?,

        @property:Property(key = "createdAt")
        val createdAt: Instant
) {
    override fun hashCode(): Int = id?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?): Boolean = id != null && other != null && other is Node && id == other.id
}
