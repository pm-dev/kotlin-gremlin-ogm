package org.apache.tinkerpop.gremlin.ogm.elements

import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship


/**
 * An [Edge] represents a [FROM] and [TO] vertex that are connected through a [Relationship].
 * [Edge] may be implemented for clients wishing to add properties to edges. In this case, the
 * [Edge] subclass must be registered with a GraphMapper alongside its [Relationship]
 */
interface Edge<out FROM : Vertex, out TO : Vertex> {

    /**
     * The out-vertex for the edge. Final subclasses must annotate the parameter
     * that sets this with @FromVertex.
     */
    val from: FROM

    /**
     * The to-vertex for the edge. Final subclasses must annotate the parameter
     * that sets this with @ToVertex.
     */
    val to: TO
}

