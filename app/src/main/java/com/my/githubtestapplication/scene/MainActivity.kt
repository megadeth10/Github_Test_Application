package com.my.githubtestapplication.scene

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.my.githubtestapplication.R
import com.my.githubtestapplication.base.BaseAlertActivity
import com.my.githubtestapplication.databinding.ActivityMainBinding
import com.my.githubtestapplication.securepreference.SecureSharedPreferences
import com.my.githubtestapplication.securepreference.SecureStorageKeyMap
import com.my.githubtestapplication.util.Log
import com.my.githubtestapplication.util.Util
import com.my.githubtestapplication.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseAlertActivity<ActivityMainBinding>(), View.OnClickListener {
    @Inject
    lateinit var secureSharedPreferences : SecureSharedPreferences
    private val mainViewModel : MainViewModel by viewModels()
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        this.contentBinding.btnLogin.setOnClickListener(this)
        this.mainViewModel.data.observe(this) {
            this@MainActivity.setResult(it)
        }
        this.mainViewModel.progress.observe(this) {
            this.visibleProgress(it)
        }
        this.setStoreData()
    }

    private fun setResult(newString: String) {
        CoroutineScope(Dispatchers.Main).launch {
            this@MainActivity.contentBinding.tvResult.text = newString
        }
    }

    private fun setStoreData() {
        Log.e(tag, "setStoreData()")
        CoroutineScope(Dispatchers.IO).launch {
            val data = secureSharedPreferences.getArrayList<String>(SecureStorageKeyMap.ID)
            val text = StringBuilder()
            data.forEach {
                text.append(it).append(" ")
            }
            Log.e(tag, "setStoreData() data: ${text.toString()}")
            displayStoreData(text.toString())
        }
    }

    private fun displayStoreData(newString: String) {
        CoroutineScope(Dispatchers.Main).launch {
            this@MainActivity.contentBinding.tvStoreData.text = newString
        }
    }

    private fun visibleProgress(newState : Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            Util.setVisible(this@MainActivity.contentBinding.flProgress, newState)
        }
    }

    override fun getContentLayoutId() = R.layout.activity_main

    override fun getLogName() = MainActivity::class.simpleName
    override fun onClick(p0 : View?) {
        when(p0?.id){
            this.contentBinding.btnLogin.id -> {
                val id = this.contentBinding.etId.text.toString()
                val pw = this.contentBinding.etPw.text.toString()
                this.mainViewModel.login(id, pw)
            }
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        finish()
    }
}