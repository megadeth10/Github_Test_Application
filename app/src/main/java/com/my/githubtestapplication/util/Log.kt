package com.my.githubtestapplication.util

import android.util.Log
import com.my.githubtestapplication.GithubApplication.Companion.isDebug

object Log {
    private const val TAG = "UI_Log"
    private fun ignoreLog() : Boolean {
        return !isDebug
    }

    fun e(tag : String?, msg : String) {
        logMessage(LEVEL.LEVEL_E, tag ?: "", msg)
    }

    fun e(tag : String?, msg : String, e : Throwable) {
        logMessage(LEVEL.LEVEL_E, tag ?: "", msg, e)
    }

    fun w(tag : String?, msg : String) {
        logMessage(LEVEL.LEVEL_W, tag ?: "", msg)
    }

    fun i(tag : String?, msg : String) {
        logMessage(LEVEL.LEVEL_I, tag ?: "", msg)
    }

    fun v(tag : String?, msg : String) {
        logMessage(LEVEL.LEVEL_V, tag ?: "", msg)
    }

    fun d(tag : String?, msg : String) {
        logMessage(LEVEL.LEVEL_D, tag ?: "", msg)
    }

    private fun logMessage(level : LEVEL, tag : String, msg : String) {
        var msg = msg
        if (ignoreLog()) {
            return
        }
        msg = "[" + TAG + "]" + msg
        when (level) {
            LEVEL.LEVEL_E -> Log.e(tag, msg)
            LEVEL.LEVEL_W -> Log.w(tag, msg)
            LEVEL.LEVEL_I -> Log.i(tag, msg)
            LEVEL.LEVEL_V -> Log.v(tag, msg)
            LEVEL.LEVEL_D -> Log.d(tag, msg)
        }
    }

    private fun logMessage(level : LEVEL, tag : String, msg : String, e : Throwable) {
        var msg = msg
        if (ignoreLog()) {
            return
        }
        msg = "[" + TAG + "]" + msg
        when (level) {
            LEVEL.LEVEL_E -> Log.e(tag, msg, e)
            LEVEL.LEVEL_W -> Log.w(tag, msg, e)
            LEVEL.LEVEL_I -> Log.i(tag, msg, e)
            LEVEL.LEVEL_V -> Log.v(tag, msg, e)
            LEVEL.LEVEL_D -> Log.d(tag, msg, e)
        }
    }

    private fun printStackTrace(
        tag : String, stack : Array<StackTraceElement?>,
        level : LEVEL
    ) {
        if (ignoreLog()) {
            return
        }
        for (element in stack) {
            if (LEVEL.LEVEL_I == level) {
                i(tag, "    at " + element.toString())
            } else if (LEVEL.LEVEL_D == level) {
                d(tag, "    at " + element.toString())
            } else if (LEVEL.LEVEL_W == level) {
                w(tag, "    at " + element.toString())
            } else if (LEVEL.LEVEL_E == level) {
                e(tag, "    at " + element.toString())
            } else if (LEVEL.LEVEL_V == level) {
                v(tag, "    at " + element.toString())
            }
        }
    }
    //    private final int LOG4J_MAX_BACKUP = 10;
    /**
     * log level<br></br>
     * LEVEL_E: error level<br></br>
     * LEVEL_W: warning level<br></br>
     * LEVEL_I: info level<br></br>
     * LEVEL_V: verbose level<br></br>
     * LEVEL_D: debug level
     */
    private enum class LEVEL {
        LEVEL_E, LEVEL_W, LEVEL_I, LEVEL_V, LEVEL_D
    }
}