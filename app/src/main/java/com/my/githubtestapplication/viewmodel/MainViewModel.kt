package com.my.githubtestapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.my.githubtestapplication.base.BaseNetworkViewModel
import com.my.githubtestapplication.dummy.DummyObject
import com.my.githubtestapplication.network.api.PostService
import com.my.githubtestapplication.securepreference.SecureSharedPreferences
import com.my.githubtestapplication.securepreference.SecureStorageKeyMap
import com.my.githubtestapplication.securepreference.callback.SecureStoreCallback
import com.my.githubtestapplication.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject

/**
 * Created by YourName on 2022/06/21.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val secureSharedPreferences : SecureSharedPreferences
) : BaseNetworkViewModel() {
    private var _data : MutableLiveData<String> = MutableLiveData<String>("")
    val data : MutableLiveData<String> = _data

    private fun setData(newString : String) {
        viewModelScope.launch {
            this@MainViewModel._data.value = newString
        }
    }

    private fun store() {
        this.setData("success!")
    }

    private fun fail() {
        this.setData("fail!")
    }

    fun login(id : String, pw : String) {
        this.setProgress(true)
        Log.e(tagName, "login()")
        viewModelScope.launch(Dispatchers.IO) {
            Thread.sleep(2000)
            this@MainViewModel.setProgress(false)
            if (DummyObject.checkLogIn(id = id, pw = pw)) {
                Log.e(tagName, "call preference")
                secureSharedPreferences.storeArrayList(
                    SecureStorageKeyMap.ID,
                    arrayListOf(id, pw),
                    callback = object: SecureStoreCallback {
                        override fun storeFinish() {
                            Log.e(tagName, "call storeFinish()")
                            store()
                        }
                    })
            } else {
                fail()

            }

        }
    }

    override fun getLogName() = MainViewModel::class.simpleName
}