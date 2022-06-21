package com.my.githubtestapplication.scene

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.my.githubtestapplication.R
import com.my.githubtestapplication.base.BaseAlertActivity
import com.my.githubtestapplication.databinding.ActivityMainBinding
import com.my.githubtestapplication.util.Util
import com.my.githubtestapplication.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseAlertActivity<ActivityMainBinding>(), View.OnClickListener {
    private val mainViewModel : MainViewModel by viewModels()
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        this.contentBinding.btnLogin.setOnClickListener(this)
        this.mainViewModel.data.observe(this, Observer {
            this@MainActivity.setResult(it)
        })
        this.mainViewModel.progress.observe(this, Observer {
            this.visibleProgress(it)
        })
    }

    private fun setResult(newString: String) {
        CoroutineScope(Dispatchers.Main).launch {
            this@MainActivity.contentBinding.tvResult.text = newString
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
}