package util.example

import org.apache.tinkerpop.gremlin.ogm.annotations.*
import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.Relationship
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.edgespec.SingleToSingleAsymmetricEdgeSpec

@Element(label = "fromIntToBool")
internal class IntToBoolEdge(

        @ID
        val id: Long? = null,

        @Property("a")
        val a: String,

        @FromVertex
        override val from: VertexWithInt,

        @ToVertex
        override val to: VertexWithBoolean

) : Edge<VertexWithInt, VertexWithBoolean> {

    override fun hashCode(): Int = id?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?): Boolean = id != null && other != null && other is IntToBoolEdge && id == other.id

    companion object {
        val fromIntToBool = SingleToSingleAsymmetricEdgeSpec<VertexWithInt, VertexWithBoolean>("fromIntToBool")
    }
}
