package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal


/**
 * This object is passed to the 'invoke' function of objects implementing the [Path] interface.
 */
class StepTraverser<FROM>(

        /**
         * The traversal that an implementor of [Path] should use to advance the traversal.
         */
        val traversal: GraphTraversal<*, FROM>,

        /**
         * The graph mapper that is managing the traversal.
         * Use this mapper to serialize/deserialize objects to/from their graph form
         */
        val graphMapper: GraphMapper
)
