package com.my.githubtestapplication.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.my.githubtestapplication.callback.IActivityCallback
import com.my.githubtestapplication.callback.ILogCallback

abstract class BaseFragment<V: ViewDataBinding>: Fragment(), IActivityCallback, ILogCallback {
    private var logName = ""
    override fun getLogName() = BaseFragment::class.simpleName
    protected var contentBinding: V? = null
    init {
        logName = this.getLogName() ?: ""
    }

    abstract fun afterViewDataBinding()

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val viewId = this.getContentLayoutId()
        contentBinding = DataBindingUtil.inflate(inflater, viewId, container, false)
        contentBinding?.lifecycleOwner = this
        this.afterViewDataBinding()
        return contentBinding?.root
    }

    override fun onDestroyView() {
        this.contentBinding = null
        super.onDestroyView()
    }
}