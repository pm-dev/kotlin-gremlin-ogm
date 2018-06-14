package org.apache.tinkerpop.gremlin.ogm.paths.relationships


/**
 * A [BaseEdge] represents a [FROM] and [TO] vertex that are connected through a [Relationship].
 * [BaseEdge] may be subclassed for clients wishing to add properties to edges. In this case, the
 * [BaseEdge] subclass must be registered with a GraphMapper alongside its [Relationship]
 */
open class BaseEdge<FROM : Any, TO : Any>(

        /**
         * The out-vertex for the edge. Final subclasses must annotate the parameter
         * that sets this with @FromVertex.
         */
        val from: FROM,

        /**
         * The to-vertex for the edge. Final subclasses must annotate the parameter
         * that sets this with @ToVertex.
         */
        val to: TO,

        /**
         * The relationship connecting the two vertices. Final subclasses should be one-to-one
         * with a specific relationship, meaning no constructor parameter is necessary and the value
         * is hard-coded when calling [BaseEdge]
         */
        val relationship: Relationship<FROM, TO>)
