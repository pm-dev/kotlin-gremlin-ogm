package starwars.models

import org.apache.tinkerpop.gremlin.ogm.annotations.*
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import org.janusgraph.graphdb.relations.RelationIdentifier

@Element(label = "siblings")
internal class Sibling(

        @ID
        id: RelationIdentifier? = null,

        @FromVertex
        from: Human,

        @ToVertex
        to: Human,

        @Property("twins")
        val twins: Boolean

) : BaseEdge<Human, Human>(id, from, to) {
    
    companion object {
        val siblings = Relationship.symmetricManyToMany<Human>(name = "siblings")
    }
}
