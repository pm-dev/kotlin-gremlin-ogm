package org.apache.tinkerpop.gremlin.ogm.rx

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.apache.tinkerpop.gremlin.ogm.traversals.*


fun <TO> GraphTraversalToSingle<TO>.rx(): Single<TO> = GraphTraversalToSingleRx(this)
fun <TO> GraphTraversalToOptional<TO>.rx(): Maybe<TO> = GraphTraversalToOptionalRx(this)
fun <TO> GraphTraversalToMany<TO>.rx(): Observable<TO> = GraphTraversalToManyRx(this)

fun <FROM, TO> MultiBoundGraphTraversalToSingle<FROM, TO>.rx(): Single<Map<FROM, TO>> = MultiBoundGraphTraversalToSingleRx(this)
fun <FROM, TO> MultiBoundGraphTraversalToOptional<FROM, TO>.rx(): Single<Map<FROM, TO?>> = MultiBoundGraphTraversalToOptionalRx(this)
fun <FROM, TO> MultiBoundGraphTraversalToMany<FROM, TO>.rx(): Single<Map<FROM, List<TO>>> = MultiBoundGraphTraversalToManyRx(this)

