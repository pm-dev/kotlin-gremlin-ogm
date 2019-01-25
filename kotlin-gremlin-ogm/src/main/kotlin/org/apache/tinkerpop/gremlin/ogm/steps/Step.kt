@file:Suppress("unused")

package org.apache.tinkerpop.gremlin.ogm.steps

import org.apache.tinkerpop.gremlin.ogm.mappers.Mapper
import org.apache.tinkerpop.gremlin.ogm.steps.bound.BoundStep
import org.apache.tinkerpop.gremlin.ogm.steps.bound.BoundStepToMany
import org.apache.tinkerpop.gremlin.ogm.steps.bound.BoundStepToOptional
import org.apache.tinkerpop.gremlin.ogm.steps.bound.BoundStepToSingle
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.SingleBoundStep
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.SingleBoundStepToMany
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.SingleBoundStepToOptional
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.SingleBoundStepToSingle
import org.apache.tinkerpop.gremlin.ogm.steps.path.PathToMany
import org.apache.tinkerpop.gremlin.ogm.steps.path.PathToOptional
import org.apache.tinkerpop.gremlin.ogm.steps.path.PathToSingle
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

/**
 * A [Step] defines manipulation to a GraphTraversal from 'FROM' object(s) to 'TO' object(s)
 */
interface Step<FROM, TO> : Mapper<StepTraverser<FROM>, GraphTraversal<*, TO>> {

    interface ToOne<FROM, TO> : Step<FROM, TO>

    interface ToSingle<FROM, TO> : ToOne<FROM, TO> {

        infix fun from(from: FROM): SingleBoundStep.ToSingle<FROM, TO> = SingleBoundStepToSingle(from, this)
        infix fun from(froms: Collection<FROM>): BoundStep.ToSingle<FROM, TO> = BoundStepToSingle(froms.toList(), this)
        fun from(vararg froms: FROM): BoundStep.ToSingle<FROM, TO> = BoundStepToSingle(froms.toList(), this)

        fun <NEXT> to(next: Step.ToSingle<TO, NEXT>): Step.ToSingle<FROM, NEXT> = PathToSingle(first = this, last = next)
        fun <NEXT> to(next: Step.ToOptional<TO, NEXT>): Step.ToOptional<FROM, NEXT> = PathToOptional(first = this, last = next)
        fun <NEXT> to(next: Step.ToMany<TO, NEXT>): Step.ToMany<FROM, NEXT> = PathToMany(first = this, last = next)

        fun filter(predicate: (TO) -> Boolean) = to(Filter(predicate))
        fun <NEXT> filterMap(map: (TO) -> NEXT?) = to(FilterMap(map))
        fun <NEXT> flatMap(map: (TO) -> Iterable<NEXT>) = to(FlatMap(map))
        fun <NEXT> map(map: (TO) -> NEXT) = to(Map(map))
    }

    interface ToOptional<FROM, TO> : ToOne<FROM, TO> {

        infix fun from(from: FROM): SingleBoundStep.ToOptional<FROM, TO> = SingleBoundStepToOptional(from, this)
        infix fun from(froms: Collection<FROM>): BoundStep.ToOptional<FROM, TO> = BoundStepToOptional(froms.toList(), this)
        fun from(vararg froms: FROM): BoundStep.ToOptional<FROM, TO> = BoundStepToOptional(froms.toList(), this)

        fun <NEXT> to(next: Step.ToOne<TO, NEXT>): Step.ToOptional<FROM, NEXT> = PathToOptional(first = this, last = next)
        fun <NEXT> to(next: Step.ToMany<TO, NEXT>): Step.ToMany<FROM, NEXT> = PathToMany(first = this, last = next)

        fun filter(predicate: (TO) -> Boolean) = to(Filter(predicate))
        fun <NEXT> filterMap(map: (TO) -> NEXT?) = to(FilterMap(map))
        fun <NEXT> flatMap(map: (TO) -> Iterable<NEXT>) = to(FlatMap(map))
        fun <NEXT> map(map: (TO) -> NEXT) = to(Map(map))
    }

    interface ToMany<FROM, TO> : Step<FROM, TO> {

        infix fun from(froms: Collection<FROM>): BoundStep.ToMany<FROM, TO> = BoundStepToMany(froms.toList(), this)
        infix fun from(from: FROM): SingleBoundStep.ToMany<FROM, TO> = SingleBoundStepToMany(from, this)
        fun from(vararg froms: FROM): BoundStep.ToMany<FROM, TO> = BoundStepToMany(froms.toList(), this)

        fun <NEXT> to(next: Step<TO, NEXT>): Step.ToMany<FROM, NEXT> = PathToMany(first = this, last = next)

        fun <NEXT> filterMap(map: (TO) -> NEXT?) = to(FilterMap(map))
        fun <NEXT> flatMap(map: (TO) -> Iterable<NEXT>) = to(FlatMap(map))
        fun <NEXT> map(map: (TO) -> NEXT) = to(Map(map))
        fun filter(predicate: (TO) -> Boolean) = to(Filter(predicate))
        fun slice(range: LongRange): Step.ToMany<FROM, TO> = to(Slice(range))
        fun sort(comparator: Comparator<TO>) = to(Sort(comparator))
        fun dedup(): Step.ToMany<FROM, TO> = to(Dedup())
    }
}

