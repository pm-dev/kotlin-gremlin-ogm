package org.apache.tinkerpop.gremlin.ogm.steps.relationship.edgespec

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex

/**
 * We restrict creating ManyToOne relationships by clients to prevent creation of a
 * ManyToOne spec that is equivalent to meaning to an already defined OneToMany
 * spec, but using a different name. To get a ManyToOne spec, define it
 * as its OneToMany equivalent then get its inverse.
 */
internal data class ManyToOptionalEdgeSpec<FROM : Vertex, TO : Vertex>(
        override val name: String
) : EdgeSpec.ManyToOptional<FROM, TO>
