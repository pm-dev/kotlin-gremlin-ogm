package org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.steps.relationship.Relationship


/**
 * Creates a [Relationship] that is uni-directional. When traversed from a 'FROM' object,
 * there will be 0 or more 'TO' objects. When the [inverse] is traversed from a 'TO' object,
 * there will be 0 or 1 'FROM' object.
 */
data class OptionalToManyEdgeSpec<FROM : Vertex, TO : Vertex>(
        override val name: String
) : EdgeSpec.OptionalToMany<FROM, TO>
