package org.apache.tinkerpop.gremlin.ogm.steps

import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal


/**
 * This object is passed to the 'invoke' function of objects implementing the Step interface.
 */
class StepTraverser<FROM>(

        /**
         * The g that an implementor of a Path should use to advance the g.
         */
        val traversal: GraphTraversal<*, FROM>,

        /**
         * The graph mapper that is managing the g.
         * Use this mapper to serialize/deserialize objects to/from their graph form
         */
        val graphMapper: GraphMapper
)
