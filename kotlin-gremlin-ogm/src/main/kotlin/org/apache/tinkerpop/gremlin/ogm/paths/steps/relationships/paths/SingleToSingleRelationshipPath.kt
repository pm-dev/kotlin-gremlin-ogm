package org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.paths

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.Relationship

internal data class SingleToSingleRelationshipPath<FROM : Vertex, MIDDLE : Vertex, TO : Vertex>(
        override val first: Relationship.SingleToSingle<FROM, MIDDLE>,
        override val last: Relationship.SingleToSingle<MIDDLE, TO>
) : RelationshipPath.SingleToSingle<FROM, MIDDLE, TO>
