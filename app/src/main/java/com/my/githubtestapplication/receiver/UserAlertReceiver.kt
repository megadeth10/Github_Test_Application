package com.my.githubtestapplication.receiver

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.StringRes
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * Toast 노출용 객체
 */
object UserAlertReceiver {
    const val ACTION_SHOW_TOAST = "action_show_toast"
    const val ACTION_SHOW_SNACKBAR = "action_show_snackbar"
    const val STRING_EXTRA = "extra_string"
    fun registerReceiver(context: Context, receiver: BroadcastReceiver?): Boolean {
        receiver?.let {
            val intentFilter = IntentFilter().apply {
                addAction(ACTION_SHOW_TOAST)
                addAction(ACTION_SHOW_SNACKBAR)
            }
            LocalBroadcastManager.getInstance(context)
                .registerReceiver(receiver, intentFilter)
            return true
        }
        return false
    }

    /**
     * 리시버 해제
     */
    fun unRegisterReceiver(context: Context, receiver: BroadcastReceiver?): Boolean {
        receiver?.let {
            LocalBroadcastManager.getInstance(context)
                .unregisterReceiver(receiver)
            return false
        }
        return true
    }

    /**
     * @param strId: String Id
     */
    fun sendAction(context: Context, @StringRes strId:Int, action:String = ACTION_SHOW_TOAST) {
        sendAction(context, context.getString(strId), action)
    }

    /**
     * @param strId: String Id
     */
    fun sendAction(activity: Activity, @StringRes strId:Int, action:String = ACTION_SHOW_TOAST) {
        sendAction(activity, activity.getString(strId), action)
    }

    fun sendAction(activity: Activity, text: String, _action:String = ACTION_SHOW_TOAST) {
        val intent = Intent().apply {
            action = action
            putExtra(STRING_EXTRA, text)
        }
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent)
    }

    /**
     * @param strId: String Id
     */
    fun sendAction(context: Context, text: String, _action:String = ACTION_SHOW_TOAST) {
        val intent = Intent().apply {
            action = _action
            putExtra(STRING_EXTRA, text)
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}