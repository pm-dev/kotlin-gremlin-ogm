package starwars.models

import org.apache.tinkerpop.gremlin.ogm.annotations.ID
import org.apache.tinkerpop.gremlin.ogm.annotations.InVertex
import org.apache.tinkerpop.gremlin.ogm.annotations.OutVertex
import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import org.janusgraph.graphdb.relations.RelationIdentifier

internal class Sibling(

        @ID
        id: RelationIdentifier? = null,

        @OutVertex
        outV: Human,

        @InVertex
        inV: Human,

        @param:Property("twins")
        @property:Property("twins")
        val twins: Boolean

) : Edge<Human, Human>(id, outV, inV, siblings) {

    companion object {
        val siblings = Relationship.symmetricManyToMany<Human>(name = "siblings")
    }
}
