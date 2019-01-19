package org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.paths

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.Relationship

internal data class OptionalToSingleRelationshipPath<FROM : Vertex, MIDDLE : Vertex, TO : Vertex>(
        override val first: Relationship.OneToSingle<FROM, MIDDLE>,
        override val last: Relationship.OneToSingle<MIDDLE, TO>
) : RelationshipPath.OptionalToSingle<FROM, MIDDLE, TO>
