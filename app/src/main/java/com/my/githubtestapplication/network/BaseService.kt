package com.my.githubtestapplication.network

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

abstract class BaseService<T> constructor(
    private var network : Network
) {
    abstract fun getInterface() : Class<T>

    fun getModule() : T = network.getRetrofit().create(getInterface())

    fun setObserver(
        singleObserver: Single<*>,
        scheduler : Scheduler = Schedulers.io(),
        observerScheduler : Scheduler = AndroidSchedulers.mainThread(),
        onSuccess : ((Any) -> Unit)? = null,
        onError : ((Throwable) -> Unit)? = null,
    ) : DisposableSingleObserver<*> {
        return singleObserver.subscribeOn(scheduler).observeOn(observerScheduler)
            .subscribeWith(object : DisposableSingleObserver<Any>() {
                override fun onSuccess(t : Any) {
                    onSuccess?.invoke(t)
                }

                override fun onError(e : Throwable) {
                    onError?.invoke(e)
                }

            })
    }
}