package com.my.githubtestapplication

import android.app.Application
import com.my.githubtestapplication.util.Log
import com.my.githubtestapplication.util.ConstValue
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GithubApplication : Application() {
    companion object {
        val isDebug =
            (BuildConfig.BUILD_TYPE === ConstValue.DEBUG) or (BuildConfig.BUILD_TYPE === ConstValue.SCREEN)
        val fcActive =
            (BuildConfig.BUILD_TYPE !== ConstValue.DEBUG) and (BuildConfig.BUILD_TYPE !== ConstValue.SCREEN)

        /**
         * 버전 타입 문자
         */
        fun getBuildType() = when (BuildConfig.BUILD_TYPE) {
            ConstValue.DEBUG -> {
                "D"
            }
            ConstValue.RELEASE -> {
                ""

            }
            else -> {
                "Q"
            }
        }
        lateinit var INSTANCE: GithubApplication
    }
    private val tag = GithubApplication::class.simpleName
    init {
        Log.e(tag, "init")
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}