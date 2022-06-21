package com.my.githubtestapplication.base

import androidx.lifecycle.ViewModel
import com.my.githubtestapplication.callback.ILogCallback

abstract class BaseViewModel: ViewModel(), ILogCallback {
    protected var tagName: String = BaseViewModel::class.simpleName ?: ""
    init {
        tagName = this.getLogName() ?: ""
    }
}