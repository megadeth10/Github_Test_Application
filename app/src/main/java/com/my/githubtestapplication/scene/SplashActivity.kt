package com.my.githubtestapplication.scene

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.marginTop
import com.my.githubtestapplication.R
import com.my.githubtestapplication.base.BaseAlertActivity
import com.my.githubtestapplication.databinding.ActivitySplashBinding
import com.my.githubtestapplication.util.Log
import com.my.githubtestapplication.util.Util
import com.my.githubtestapplication.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseAlertActivity<ActivitySplashBinding>(), View.OnClickListener, CoroutineScope {
    private val splashViewModel: SplashViewModel by viewModels()
    private val random = Random(100)
    private val clickCoroutineScope = CoroutineScope(Job() + Dispatchers.Main)
    private val channelCoroutineScope = CoroutineScope(Job() + Dispatchers.Main)
    private val testChannel = Channel<Int>()
    private val ioScope = CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY) {
        delay(5_000)
        Log.e(tag, "ioScope()")
    }
    override fun getContentLayoutId() = R.layout.activity_splash

    override fun getLogName() = SplashActivity::class.simpleName

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        this.contentBinding.btnWithContext.setOnClickListener(this)
        this.contentBinding.btnEmpty.setOnClickListener(this)
        this.contentBinding.btnTestChannel.setOnClickListener(this)
        this.contentBinding.btnTestException.setOnClickListener(this)
        this.contentBinding.btnNext.setOnClickListener(this)
        this.startAnimation()
        splashViewModel.resultText.observe(this) {
            it?.let{
                this.contentBinding.tvResult.text = it
            }
        }
    }

    override fun onStart() {
        launch(coroutineContext) {
            registedChannelReceiver()
        }
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        splashViewModel.fetchAllStart()
//        splashViewModel.fetchTwoApi()
        Log.e(tag, "onResume()")
        clickCoroutineScope.launch {
            ioScope.join()
        }

        ioScope.invokeOnCompletion {
            when(it) {
                is CancellationException -> Log.e(tag,"ioScope cancel()")
                null -> Log.e(tag,"ioScope complete()")
            }
        }
    }

    /**
     * splash 이미지 애니메이션
     */
    private fun startAnimation() {
        splashViewModel.setAnimation(false)
        this.contentBinding.logo.animate().setDuration(1000)
            .alpha(1f)
            .translationY(-(this.contentBinding.logo.height).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    Log.e(tag, "onAnimationEnd()")
                    splashViewModel.setAnimation(true)
                }
            })
            .start()
    }

    /**
     * animation Stop
     */
    private fun stopAnimation() {
        this.contentBinding.logo.animate().cancel()
    }

    override fun onDestroy() {
        this.job.cancel()
        this.stopAnimation()
        super.onDestroy()
    }

    override fun onBackPressed() {
        finish()
    }

    private fun setProgressText(newText: String) {
        this.contentBinding.tvProgress.text = newText
    }

    private fun setEmptyText(newText: String) {
        this.contentBinding.tvEmpty.text = newText
    }

    private fun startTask() {
        clickCoroutineScope.launch(Dispatchers.Main + coroutineContext) {
            setEmptyText(performSlowTaskAsync().await())
        }
    }

    // async return value
    private suspend fun performSlowTaskAsync(): Deferred<String> = clickCoroutineScope.async(Dispatchers.Default + coroutineContext) {
        Log.e(tag,"performSlowTaskAsync() before")
        delay(7_777)
        Log.e(tag,"performSlowTaskAsync() after")
        return@async "Finish"
    }

    // coroutine channel receive
    private suspend fun registedChannelReceiver() {
        channelCoroutineScope.launch(Dispatchers.Main) {
            Log.e(tag,"registedChannelReceiver() received: ${testChannel.receive()}")
        }
    }

    // coroutine channel send
    private suspend fun sendChannel() {
        channelCoroutineScope.launch(Dispatchers.Main) {
            testChannel.send(1)
        }
    }

    override fun onClick(v : View?) {
        when(v?.id) {
            this.contentBinding.btnWithContext.id -> {
                splashViewModel.stopContextJob()
                this.setProgressText("0")
                CoroutineScope(Dispatchers.Default).launch {
                    splashViewModel.testWithContext(::setProgressText)
                }
                Log.e(tag, "btnWithContext click")
            }
            this.contentBinding.btnEmpty.id -> {
//                setEmptyText(random.nextInt().toString() + "aaaa")
                startTask()
                Log.e(tag, "btnEmpty click")
            }
            this.contentBinding.btnTestChannel.id -> {
                CoroutineScope(Dispatchers.Main).launch {
                    sendChannel()
                }
                Log.e(tag, "btnTestChannel click")
            }
            this.contentBinding.btnTestException.id -> {
                Log.e(tag, "btnTestException click")
                CoroutineScope(coroutineContext + Dispatchers.Main + exceptionHandler).launch {
                    exceptionFunction()
                }
            }
            this.contentBinding.btnNext.id -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private suspend fun exceptionFunction() {
        throw Exception("test exception")
    }

    private val exceptionHandler = CoroutineExceptionHandler { scope, exception ->
        Log.e(tag, "exceptionHandler ", exception)
    }

    // activity scope 설정
    private var job = Job()
    override val coroutineContext : CoroutineContext
        get() = Dispatchers.Main + job
}