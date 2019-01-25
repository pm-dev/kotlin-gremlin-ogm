package org.apache.tinkerpop.gremlin.ogm.steps.path.relationship

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship

internal data class SingleToManyRelationshipPath<FROM : Vertex, MIDDLE : Vertex, TO : Vertex>(
        override val first: Relationship.FromSingle<FROM, MIDDLE>,
        override val last: Relationship.FromSingle<MIDDLE, TO>
) : RelationshipPath.SingleToMany<FROM, MIDDLE, TO>
