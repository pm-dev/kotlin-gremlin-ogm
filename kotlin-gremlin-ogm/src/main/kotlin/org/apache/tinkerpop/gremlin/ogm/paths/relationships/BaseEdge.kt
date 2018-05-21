package org.apache.tinkerpop.gremlin.ogm.paths.relationships


/**
 * A [BaseEdge] represents a [OUT] and [IN] vertex that are connected through a [Relationship].
 * [BaseEdge] may be subclassed for clients wishing to add properties to edges. In this case, the
 * [BaseEdge] subclass must be registered with a GraphMapper alongside its [Relationship]
 */
open class BaseEdge<OUT : Any, IN : Any>(

        /**
         * The out-vertex for the edge. Final subclasses must annotate the parameter
         * that sets this with @OutVertex.
         */
        val outV: OUT,

        /**
         * The in-vertex for the edge. Final subclasses must annotate the parameter
         * that sets this with @InVertex.
         */
        val inV: IN,

        /**
         * The relationship connecting the two vertices. Final subclasses should be one-to-one
         * with a specific relationship, meaning no constructor parameter is necessary and the value
         * is hard-coded when calling [BaseEdge]
         */
        val relationship: Relationship<OUT, IN>)
