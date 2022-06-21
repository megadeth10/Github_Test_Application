package com.my.githubtestapplication.util

import android.app.Activity.RESULT_CANCELED
import android.app.Instrumentation

/**
 * Created by YourName on 2022/06/13.
 */
object AppActivityResultCode{
    private const val BaseResultCode = RESULT_CANCELED.plus(1)
    const val ResultCodeBack = BaseResultCode.plus(1)
}