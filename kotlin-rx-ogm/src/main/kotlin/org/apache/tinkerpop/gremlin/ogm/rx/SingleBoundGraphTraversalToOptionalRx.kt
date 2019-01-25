package org.apache.tinkerpop.gremlin.ogm.rx

import io.reactivex.Maybe
import io.reactivex.MaybeObserver
import io.reactivex.disposables.Disposables
import io.reactivex.exceptions.Exceptions
import io.reactivex.plugins.RxJavaPlugins
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.SingleBoundStep

internal data class SingleBoundGraphTraversalToOptionalRx<TO>(
        private val mapper: GraphMapper,
        private val boundStep: SingleBoundStep.ToOptional<*, TO>
) : Maybe<TO>() {

    override fun subscribeActual(observer: MaybeObserver<in TO>) {
        val d = Disposables.empty()
        observer.onSubscribe(d)
        if (d.isDisposed) {
            return
        }
        val v: TO?
        try {
            v = mapper.traverse(boundStep)
        } catch (ex: Throwable) {
            Exceptions.throwIfFatal(ex)
            if (d.isDisposed) {
                RxJavaPlugins.onError(ex)
            } else {
                observer.onError(ex)
            }
            return
        }
        if (d.isDisposed) {
            return
        }
        if (v == null) {
            observer.onComplete()
        } else {
            observer.onSuccess(v)
        }
    }
}
