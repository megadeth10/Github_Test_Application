package com.my.githubtestapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.my.githubtestapplication.base.BaseNetworkViewModel
import com.my.githubtestapplication.dummy.DummyObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by YourName on 2022/06/21.
 */
class MainViewModel : BaseNetworkViewModel() {
    private var _data : MutableLiveData<String> = MutableLiveData<String>("")
    val data : MutableLiveData<String> = _data

    private fun setData(newString : String) {
        viewModelScope.launch {
            this@MainViewModel._data.value = newString
        }
    }

    fun login(id : String, pw : String) {
        this.setProgress(true)
        CoroutineScope(Dispatchers.IO).launch {
            Thread.sleep(2000)
            if (DummyObject.checkLogIn(id = id, pw = pw)) {
                this@MainViewModel.setData("success!")
            } else {
                this@MainViewModel.setData("fail!")
            }
            this@MainViewModel.setProgress(false)
        }
    }

    override fun getLogName() = MainViewModel::class.simpleName
}