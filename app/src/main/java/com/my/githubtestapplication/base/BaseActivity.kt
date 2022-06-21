package com.my.githubtestapplication.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.my.githubtestapplication.callback.IActivityCallback
import com.my.githubtestapplication.callback.ILogCallback
import com.my.githubtestapplication.util.Log

abstract class BaseActivity<V: ViewDataBinding>: AppCompatActivity(), IActivityCallback, ILogCallback {
    protected var tag = BaseActivity::class.simpleName
    protected lateinit var contentBinding: V

    init {
        tag = this.getLogName() ?: ""
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        this.setViewDataBinding()
        Log.e(tag, "onCreate()")
    }


    private fun setViewDataBinding() {
        this.contentBinding = DataBindingUtil.setContentView(this, this.getContentLayoutId())
        this.contentBinding.lifecycleOwner = this
    }


}