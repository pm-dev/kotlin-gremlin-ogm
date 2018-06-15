package starwars.models

import org.apache.tinkerpop.gremlin.ogm.annotations.ToVertex
import org.apache.tinkerpop.gremlin.ogm.annotations.ID
import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import org.apache.tinkerpop.gremlin.ogm.annotations.FromVertex
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship
import org.janusgraph.graphdb.relations.RelationIdentifier

internal class Sibling(

        @ID
        private val id: RelationIdentifier? = null,

        @FromVertex
        from: Human,

        @ToVertex
        to: Human,

        @Property("twins")
        val twins: Boolean

) : Edge<Human, Human>(id, from, to, siblings) {

    companion object {
        val siblings = Relationship.symmetricManyToMany<Human>(name = "siblings")
    }
}
