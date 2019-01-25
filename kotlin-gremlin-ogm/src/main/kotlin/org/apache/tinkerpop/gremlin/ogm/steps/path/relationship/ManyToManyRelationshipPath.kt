package org.apache.tinkerpop.gremlin.ogm.steps.path.relationship

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship

internal data class ManyToManyRelationshipPath<FROM : Vertex, MIDDLE : Vertex, TO : Vertex>(
        override val first: Relationship<FROM, MIDDLE>,
        override val last: Relationship<MIDDLE, TO>
) : RelationshipPath.ManyToMany<FROM, MIDDLE, TO>
