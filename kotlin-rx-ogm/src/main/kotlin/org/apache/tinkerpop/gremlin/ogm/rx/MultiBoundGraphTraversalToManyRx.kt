package org.apache.tinkerpop.gremlin.ogm.rx

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposables
import io.reactivex.exceptions.Exceptions
import io.reactivex.plugins.RxJavaPlugins
import org.apache.tinkerpop.gremlin.ogm.traversals.MultiBoundGraphTraversalToMany

internal class MultiBoundGraphTraversalToManyRx<FROM, TO>(
        private val traversal: MultiBoundGraphTraversalToMany<FROM, TO>
) : Single<Map<FROM, List<TO>>>() {


    override fun subscribeActual(observer: SingleObserver<in Map<FROM, List<TO>>>) {
        val d = Disposables.empty()
        observer.onSubscribe(d)
        if (d.isDisposed) {
            return
        }
        val v: Map<FROM, List<TO>>
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
