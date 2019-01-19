package org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.paths

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.Relationship

internal data class OptionalToOptionalRelationshipPath<FROM : Vertex, MIDDLE : Vertex, TO : Vertex>(
        override val first: Relationship.OneToOne<FROM, MIDDLE>,
        override val last: Relationship.OneToOne<MIDDLE, TO>
) : RelationshipPath.OptionalToOptional<FROM, MIDDLE, TO>
