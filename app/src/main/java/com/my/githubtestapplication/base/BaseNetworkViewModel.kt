package com.my.githubtestapplication.base

import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import com.my.githubtestapplication.network.NetworkInterface
import io.reactivex.rxjava3.observers.DisposableSingleObserver

/**
 * Created by YourName on 2022/06/21.
 */
abstract class BaseNetworkViewModel : BaseViewModel(), NetworkInterface {
    private var networkDisposable = HashMap<String, DisposableSingleObserver<*>>(0)

    private val _progress : MutableLiveData<Boolean> = MutableLiveData(false)
    val progress : MutableLiveData<Boolean> = _progress

    /**
     * 옵져버 추가
     */
    final override fun addObserver(name : String, observer : DisposableSingleObserver<*>) {
        this.networkDisposable[name] = observer
    }

    /**
     * 통신 종료
     */
    final override fun cancelObserver(name : String) {
        val observer = this.networkDisposable[name]
        observer?.let {
            it.dispose()
            this.networkDisposable.remove(name)
        }
    }

    /**
     * 모든 통신 종료
     */
    final override fun cancelNetworkAll() {
        this.networkDisposable.forEach {
            it.value.dispose()
        }
    }

    protected fun setProgress(newState : Boolean) {
        if (newState != this.progress.value) {
            this._progress.postValue(newState)
        }
    }

    @CallSuper
    override fun onCleared() {
        this.cancelNetworkAll()
        super.onCleared()
    }
}