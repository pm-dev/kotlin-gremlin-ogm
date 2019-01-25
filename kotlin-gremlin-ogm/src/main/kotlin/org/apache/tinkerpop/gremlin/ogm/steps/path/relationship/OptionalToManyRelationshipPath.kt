package org.apache.tinkerpop.gremlin.ogm.steps.path.relationship

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship

internal data class OptionalToManyRelationshipPath<FROM : Vertex, MIDDLE : Vertex, TO : Vertex>(
        override val first: Relationship.FromOne<FROM, MIDDLE>,
        override val last: Relationship.FromOne<MIDDLE, TO>
) : RelationshipPath.OptionalToMany<FROM, MIDDLE, TO>
