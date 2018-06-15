package starwars.models

import java.time.Instant

internal abstract class Node(
        val id: Long?,
        val createdAt: Instant
) {
    override fun hashCode(): Int = id?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?): Boolean = id != null && other != null && other is Node && id == other.id
}
