@file:Suppress("unused")

package org.apache.tinkerpop.gremlin.ogm.rx

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.steps.bound.BoundStep
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.SingleBoundStep

fun <TO> GraphMapper.traverseRx(step: SingleBoundStep.ToSingle<*, TO>): Single<TO> = SingleBoundGraphTraversalToSingleRx(this, step)
fun <TO> GraphMapper.traverseRx(step: SingleBoundStep.ToOptional<*, TO>): Maybe<TO> = SingleBoundGraphTraversalToOptionalRx(this, step)
fun <TO> GraphMapper.traverseRx(step: SingleBoundStep.ToMany<*, TO>): Observable<TO> = SingleBoundGraphTraversalToManyRx(this, step)
fun <FROM, TO> GraphMapper.traverseRx(step: BoundStep.ToSingle<FROM, TO>): Single<Map<FROM, TO>> = MultiBoundGraphTraversalToSingleRx(this, step)
fun <FROM, TO> GraphMapper.traverseRx(step: BoundStep.ToOptional<FROM, TO>): Single<Map<FROM, TO?>> = MultiBoundGraphTraversalToOptionalRx(this, step)
fun <FROM, TO> GraphMapper.traverseRx(step: BoundStep.ToMany<FROM, TO>): Single<Map<FROM, List<TO>>> = MultiBoundGraphTraversalToManyRx(this, step)
