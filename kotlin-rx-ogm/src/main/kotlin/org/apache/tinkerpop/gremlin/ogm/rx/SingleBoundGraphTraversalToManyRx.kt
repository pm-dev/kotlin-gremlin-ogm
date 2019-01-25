package org.apache.tinkerpop.gremlin.ogm.rx

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.exceptions.Exceptions
import io.reactivex.internal.disposables.EmptyDisposable
import io.reactivex.internal.fuseable.QueueFuseable
import io.reactivex.internal.observers.BasicQueueDisposable
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.steps.bound.single.SingleBoundStep
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal

internal data class SingleBoundGraphTraversalToManyRx<TO>(
        private val mapper: GraphMapper,
        private val boundStep: SingleBoundStep.ToMany<*, TO>
) : Observable<TO>() {

    override fun subscribeActual(observer: Observer<in TO>) {
        val hasNext: Boolean
        val traversal: GraphTraversal<*, TO>
        try {
            traversal = mapper.traversal(boundStep).map { it.get().second }
            hasNext = traversal.hasNext()
        } catch (e: Throwable) {
            Exceptions.throwIfFatal(e)
            EmptyDisposable.error(e, observer)
            return
        }
        if (!hasNext) {
            EmptyDisposable.complete(observer)
            return
        }
        val d = FromTraversalDisposable(observer, traversal)
        observer.onSubscribe(d)
        if (!d.fusionMode) {
            d.run()
        }
    }

    private class FromTraversalDisposable<TO>(
            private val downstream: Observer<in TO>,
            private val traversal: GraphTraversal<*, TO>
    ) : BasicQueueDisposable<TO>() {

        @Volatile
        var disposed: Boolean = false

        var fusionMode: Boolean = false

        private var done: Boolean = false

        private var checkNext: Boolean = false

        fun run() {
            var hasNext: Boolean

            do {
                if (isDisposed) {
                    return
                }
                val v: TO

                try {
                    v = traversal.next()
                } catch (e: Throwable) {
                    Exceptions.throwIfFatal(e)
                    downstream.onError(e)
                    return
                }

                downstream.onNext(v)

                if (isDisposed) {
                    return
                }
                try {
                    hasNext = traversal.hasNext()
                } catch (e: Throwable) {
                    Exceptions.throwIfFatal(e)
                    downstream.onError(e)
                    return
                }

            } while (hasNext)

            if (!isDisposed) {
                downstream.onComplete()
            }
        }

        override fun requestFusion(mode: Int): Int {
            if (mode and QueueFuseable.SYNC != 0) {
                fusionMode = true
                return QueueFuseable.SYNC
            }
            return QueueFuseable.NONE
        }

        override fun poll(): TO? {
            if (done) {
                return null
            }
            if (checkNext) {
                if (!traversal.iterator().hasNext()) {
                    done = true
                    return null
                }
            } else {
                checkNext = true
            }

            return traversal.iterator().next()
        }

        override fun isEmpty(): Boolean = done

        override fun clear() {
            done = true
        }

        override fun dispose() {
            disposed = true
        }

        override fun isDisposed(): Boolean = disposed
    }
}
