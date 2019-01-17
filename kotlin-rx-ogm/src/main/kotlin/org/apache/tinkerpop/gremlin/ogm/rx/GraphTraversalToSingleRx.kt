package org.apache.tinkerpop.gremlin.ogm.rx

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposables
import io.reactivex.exceptions.Exceptions
import io.reactivex.plugins.RxJavaPlugins
import org.apache.tinkerpop.gremlin.ogm.traversals.GraphTraversalToSingle

internal class GraphTraversalToSingleRx<TO>(
        private val traversal: GraphTraversalToSingle<TO>
) : Single<TO>() {

    override fun subscribeActual(observer: SingleObserver<in TO>) {
        val d = Disposables.empty()
        observer.onSubscribe(d)
        if (d.isDisposed) {
            return
        }
        val v: TO
        try {
            v = traversal.traverse()
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
