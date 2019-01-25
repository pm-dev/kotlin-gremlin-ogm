package org.apache.tinkerpop.gremlin.ogm.steps.path.relationship

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship

internal data class ManyToOptionalRelationshipPath<FROM : Vertex, MIDDLE : Vertex, TO : Vertex>(
        override val first: Relationship.ToOne<FROM, MIDDLE>,
        override val last: Relationship.ToOne<MIDDLE, TO>
) : RelationshipPath.ManyToOptional<FROM, MIDDLE, TO>
