package org.apache.tinkerpop.gremlin.ogm.steps.path.relationship

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship

internal data class ManyToSingleRelationshipPath<FROM : Vertex, MIDDLE : Vertex, TO : Vertex>(
        override val first: Relationship.ToSingle<FROM, MIDDLE>,
        override val last: Relationship.ToSingle<MIDDLE, TO>
) : RelationshipPath.ManyToSingle<FROM, MIDDLE, TO>
