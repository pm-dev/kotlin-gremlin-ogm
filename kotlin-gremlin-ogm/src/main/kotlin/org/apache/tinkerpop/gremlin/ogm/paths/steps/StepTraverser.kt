package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.mappers.BiMapper
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.BaseEdge
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Vertex

/**
 * This object is passed to the 'invoke' function of objects implementing the [Path] interface.
 */
class StepTraverser<FROM>(

        /**
         * The traversal that an implementor of [Path] should use to advance the traversal.
         */
        val traversal: GraphTraversal<*, FROM>,

        /**
         * A generic object which can map objects to/from their vertex form. If the vertexMapper is forward mapped with
         * an object whose class is not registered with GraphMapper, the vertexMapper will throw an exception. Similarly,
         * if the vertexMapper is inverse mapped with a vertex whose label is not present on a class annotated
         * with @Vertex and registered with GraphMapper, an exception is thrown.
         * See the 'Connection' interface for an example of how this can be used.
         */
        val vertexMapper: BiMapper<Any, Vertex>,

        val edgeMapper: BiMapper<BaseEdge<Any, Any>, Edge>
)
