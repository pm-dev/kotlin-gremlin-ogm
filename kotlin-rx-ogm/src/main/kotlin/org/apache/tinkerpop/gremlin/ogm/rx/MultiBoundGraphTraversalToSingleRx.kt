package org.apache.tinkerpop.gremlin.ogm.rx

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposables
import io.reactivex.exceptions.Exceptions
import io.reactivex.plugins.RxJavaPlugins
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.steps.bound.BoundStep

internal data class MultiBoundGraphTraversalToSingleRx<FROM, TO>(
        private val mapper: GraphMapper,
        private val boundStep: BoundStep.ToSingle<FROM, TO>
) : Single<Map<FROM, TO>>() {

    override fun subscribeActual(observer: SingleObserver<in Map<FROM, TO>>) {
        val d = Disposables.empty()
        observer.onSubscribe(d)
        if (d.isDisposed) {
            return
        }
        val v: Map<FROM, TO>
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
        observer.onSuccess(v)
    }
}
