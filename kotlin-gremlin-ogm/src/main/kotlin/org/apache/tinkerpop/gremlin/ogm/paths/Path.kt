package org.apache.tinkerpop.gremlin.ogm.paths

import org.apache.tinkerpop.gremlin.ogm.mappers.Mapper
import org.apache.tinkerpop.gremlin.ogm.paths.steps.StepTraverser
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A path represents a [GraphTraversal] transformation from 'OUT' objects to 'IN' objects
 */
interface Path<OUT, IN> : Mapper<StepTraverser<OUT>, GraphTraversal<*, IN>> {

    /**
     * The list of sub-paths that comprise this path. It is possible for this path to
     * be the only path in the list.
     */
    fun path(): List<Path<*, *>>

    /**
     * A path that is either a [ToOptional] or [ToSingle]
     */
    interface ToOne<OUT, IN> : Path<OUT, IN>

    /**
     * A path that does not change the number of objects that would result from the current traversal
     */
    interface ToSingle<OUT, IN> : ToOne<OUT, IN>

    /**
     * A path that may reduce the number of objects that would result from the current traversal
     */
    interface ToOptional<OUT, IN> : ToOne<OUT, IN>

    /**
     * A path that may increase the number of objects that would result from the
     */
    interface ToMany<OUT, IN> : Path<OUT, IN>
}
