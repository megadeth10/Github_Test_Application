package com.my.githubtestapplication.scene

import android.os.Bundle
import androidx.activity.viewModels
import com.my.githubtestapplication.R
import com.my.githubtestapplication.base.BaseAlertActivity
import com.my.githubtestapplication.databinding.ActivityMainBinding
import com.my.githubtestapplication.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseAlertActivity<ActivityMainBinding>() {
    private val mainViewModel : MainViewModel by viewModels()
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getContentLayoutId() = R.layout.activity_main

    override fun getLogName() = MainActivity::class.simpleName
}