package com.my.githubtestapplication.scene

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import com.my.githubtestapplication.R
import com.my.githubtestapplication.base.BaseAlertActivity
import com.my.githubtestapplication.databinding.ActivitySplashBinding
import com.my.githubtestapplication.util.Log
import com.my.githubtestapplication.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseAlertActivity<ActivitySplashBinding>() {
    private val splashViewModel: SplashViewModel by viewModels()
    override fun getContentLayoutId() = R.layout.activity_splash

    override fun getLogName() = SplashActivity::class.simpleName

    override fun onStart() {
        this.startAnimation()
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        splashViewModel.fetchAllStart()
//        splashViewModel.fetchTwoApi()
    }

    /**
     * splash 이미지 애니메이션
     */
    private fun startAnimation() {
        splashViewModel.setAnimation(false)
        this@SplashActivity.contentBinding.logo.animate().setDuration(1000)
            .alpha(1f)
            .translationY(-(this@SplashActivity.contentBinding.logo.height).toFloat())
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
        this.stopAnimation()
        super.onDestroy()
    }

    override fun onBackPressed() {
        finish()
    }
}