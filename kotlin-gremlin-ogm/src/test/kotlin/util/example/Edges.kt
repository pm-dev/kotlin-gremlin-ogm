package util.example

import org.apache.tinkerpop.gremlin.ogm.annotations.ID
import org.apache.tinkerpop.gremlin.ogm.annotations.InVertex
import org.apache.tinkerpop.gremlin.ogm.annotations.OutVertex
import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.BaseEdge
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

internal class IntToBoolEdge(

        @param:ID
        @property:ID
        val id: Long? = null,

        @param:Property("a")
        @property:Property("a")
        val a: String,

        @OutVertex
        outV: VertexWithInt,

        @InVertex
        inV: VertexWithBoolean

) : BaseEdge<VertexWithInt, VertexWithBoolean>(outV, inV, fromIntToBool) {

    override fun hashCode(): Int = id?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?): Boolean = id != null && other != null && other is IntToBoolEdge && id == other.id

    companion object {
        val fromIntToBool = Relationship.asymmetricSingleToSingle<VertexWithInt, VertexWithBoolean>("fromIntToBool")
    }
}
