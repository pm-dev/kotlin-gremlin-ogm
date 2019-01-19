package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.mappers.Mapper
import org.apache.tinkerpop.gremlin.ogm.paths.steps.paths.PathToMany
import org.apache.tinkerpop.gremlin.ogm.paths.steps.paths.PathToOptional
import org.apache.tinkerpop.gremlin.ogm.paths.steps.paths.PathToSingle
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A [Step] defines manipulation to a GraphTraversal from 'FROM' object(s) to 'TO' object(s)
 */
interface Step<FROM, TO> : Mapper<StepTraverser<FROM>, GraphTraversal<*, TO>> {

    interface ToOne<FROM, TO> : Step<FROM, TO>

    interface ToSingle<FROM, TO> : ToOne<FROM, TO> {

        fun <NEXT> to(next: Step.ToSingle<TO, NEXT>): Step.ToSingle<FROM, NEXT> = PathToSingle(first = this, last = next)

        fun <NEXT> to(next: Step.ToOptional<TO, NEXT>): Step.ToOptional<FROM, NEXT> = PathToOptional(first = this, last = next)

        fun <NEXT> to(next: Step.ToMany<TO, NEXT>): Step.ToMany<FROM, NEXT> = PathToMany(first = this, last = next)
    }

    interface ToOptional<FROM, TO> : ToOne<FROM, TO> {

        fun <NEXT> to(next: Step.ToOne<TO, NEXT>): Step.ToOptional<FROM, NEXT> = PathToOptional(first = this, last = next)

        fun <NEXT> to(next: Step.ToMany<TO, NEXT>): Step.ToMany<FROM, NEXT> = PathToMany(first = this, last = next)
    }

    interface ToMany<FROM, TO> : Step<FROM, TO> {

        fun <NEXT> to(next: Step<TO, NEXT>): Step.ToMany<FROM, NEXT> = PathToMany(first = this, last = next)
    }
}

