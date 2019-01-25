package org.apache.tinkerpop.gremlin.ogm.steps.path.relationship

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship

internal data class SingleToOptionalRelationshipPath<FROM : Vertex, MIDDLE : Vertex, TO : Vertex>(
        override val first: Relationship.SingleToOne<FROM, MIDDLE>,
        override val last: Relationship.SingleToOne<MIDDLE, TO>
) : RelationshipPath.SingleToOptional<FROM, MIDDLE, TO>
