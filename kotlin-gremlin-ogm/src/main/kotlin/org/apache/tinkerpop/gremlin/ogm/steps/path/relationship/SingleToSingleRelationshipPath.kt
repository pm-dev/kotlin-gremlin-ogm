package org.apache.tinkerpop.gremlin.ogm.steps.path.relationship

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship

internal data class SingleToSingleRelationshipPath<FROM : Vertex, MIDDLE : Vertex, TO : Vertex>(
        override val first: Relationship.SingleToSingle<FROM, MIDDLE>,
        override val last: Relationship.SingleToSingle<MIDDLE, TO>
) : RelationshipPath.SingleToSingle<FROM, MIDDLE, TO>
