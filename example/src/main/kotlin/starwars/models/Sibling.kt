package starwars.models

import framework.BaseEdge
import org.apache.tinkerpop.gremlin.ogm.annotations.*
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec.ManyToManySymmetricEdgeSpec
import org.janusgraph.graphdb.relations.RelationIdentifier

@Element(label = "siblings")
internal data class Sibling(

        @ID
        override val id: RelationIdentifier? = null,

        @FromVertex
        override val from: Human,

        @ToVertex
        override val to: Human,

        @Property("twins")
        val twins: Boolean

) : BaseEdge<Human, Human>() {
    
    companion object {
        val siblings = ManyToManySymmetricEdgeSpec<Human>(name = "siblings")
    }
}
