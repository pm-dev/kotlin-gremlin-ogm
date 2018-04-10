package org.apache.tinkerpop.gremlin.ogm.relationships.steps

import org.apache.tinkerpop.gremlin.ogm.mappers.Mapper
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A path represents a [GraphTraversal] transformation from 'FROM' objects to 'TO' objects
 */
interface Path<FROM, TO> : Mapper<StepTraverser<FROM>, GraphTraversal<*, TO>> {

    fun path(): List<Path<*, *>>

    interface ToOne<FROM, TO> : Path<FROM, TO>

    /**
     * A path that does not change the number of objects that would result from the current traversal
     */
    interface ToSingle<FROM, TO> : ToOne<FROM, TO>

    /**
     * A path that may reduce the number of objects that would result from the current traversal
     */
    interface ToOptional<FROM, TO> : ToOne<FROM, TO>

    /**
     * A path that may increase the number of objects that would result from the
     */
    interface ToMany<FROM, TO> : Path<FROM, TO>
}
