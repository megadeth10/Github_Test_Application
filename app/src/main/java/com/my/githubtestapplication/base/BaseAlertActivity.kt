package com.my.githubtestapplication.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.ViewDataBinding
import com.androidadvance.topsnackbar.TSnackbar
import com.google.android.material.snackbar.Snackbar
import com.my.githubtestapplication.R
import com.my.githubtestapplication.receiver.UserAlertReceiver
import com.my.githubtestapplication.util.KeyboardVisibility
import com.my.githubtestapplication.util.Util

/**
 * Created by YourName on 2022/06/13.
 */
abstract class BaseAlertActivity<V: ViewDataBinding>: BaseActivity<V>() {
    /** user alert */
    private var snackbar: Any? = null
    private var toast: Toast? = null
    private var dialog: AppCompatDialog? = null
    protected var snackbarText: String? = null
    private var hasAlertReceiver = false
    /** keyboard */
    protected var keyboardVisibility: KeyboardVisibility? = null
    protected var softNavigationBarHeight = 0
    protected var onShowKeyboard: ((Int) -> Unit)? = null
    protected var onHideKeyboard: (() -> Unit)? = null
    protected var showKeyboard = false

    private var alertReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                UserAlertReceiver.ACTION_SHOW_TOAST -> {
                    if (intent.hasExtra(UserAlertReceiver.STRING_EXTRA)) {
                        intent.getStringExtra(UserAlertReceiver.STRING_EXTRA)?.let { text ->
                            showToast(text)
                        }
                    }
                }
                UserAlertReceiver.ACTION_SHOW_SNACKBAR -> {
                    if (intent.hasExtra(UserAlertReceiver.STRING_EXTRA)) {
                        intent.getStringExtra(UserAlertReceiver.STRING_EXTRA)?.let { text ->
                            showSnackbar(text)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        initValue()
    }

    override fun onResume() {
        if (!this.hasAlertReceiver) {
            this.hasAlertReceiver = UserAlertReceiver.registerReceiver(this, this.alertReceiver)
        }
        super.onResume()
    }

    override fun onPause() {
        if (this.hasAlertReceiver) {
            this.hasAlertReceiver = UserAlertReceiver.unRegisterReceiver(this, this.alertReceiver)
        }
        super.onPause()
    }

    /** =============== keyboard ===============  */
    private fun initValue() {
        if (this.keyboardVisibility == null) {
            this.keyboardVisibility = KeyboardVisibility(window,
                onShowKeyboard = { keyboardHeight ->
                    this.showKeyboard = true
                    this.onShowKeyboard?.invoke(keyboardHeight)
                },
                onHideKeyboard = {
                    this.showKeyboard = false
                    this.onHideKeyboard?.invoke()
                }
            )
        }
        this.softNavigationBarHeight = Util.getSoftNavigationBarHeight(this)
    }

    /** =============== toast ===============  */
    protected fun closeToast() {
        this.toast?.cancel()
    }

    protected fun showToast(@StringRes strId: Int, duration:Int = Toast.LENGTH_SHORT) {
        this.showToast(getString(strId), duration)
    }

    protected fun showToast(text: String, duration:Int = Toast.LENGTH_SHORT) {
        this.closeToast()
        this.toast = Toast.makeText(this, text, duration)
        this.toast?.show()
    }

    /** =============== snackbar ===============  */

    open class BaseSnackBarCallback {
        var tsbCallback = TSnackBarCallback()
        var sbCallback = SnackBarCallback()
        var callback: IBaseSnackBarCallback? = null

        inner class TSnackBarCallback : TSnackbar.Callback() {
            override fun onShown(snackbar: TSnackbar?) {
                callback?.shown()
            }

            override fun onDismissed(snackbar: TSnackbar?, event: Int) {
                callback?.dismissed()
            }
        }

        inner class SnackBarCallback : Snackbar.Callback() {
            override fun onShown(snackbar: Snackbar?) {
                callback?.shown()
            }

            override fun onDismissed(snackbar: Snackbar?, event: Int) {
                callback?.dismissed()
            }
        }
    }

    /**
     * TSnackBar, SnackBar Show/Dismiss 공통 Callback
     */
    interface IBaseSnackBarCallback {
        fun shown()
        fun dismissed()
    }

    /**
     * Snackbar text color
     */
    private fun getSnackBarTextColor(): Int = Util.getColor(this@BaseAlertActivity, R.color.white)

    /**
     * Snackbar action text color
     */
    private fun getSnackBarActionTextColor(): Int = Util.getColor(this@BaseAlertActivity, R.color.white)

    // Snackbar background color
    private fun getSnackBarBackgroundColor(): Int = Util.getColor(this@BaseAlertActivity, R.color.red_point)

    /**
     * 상단 Snackbar 생성
     */
    private fun makeTSnackBar(anchorView: View, text: String): TSnackbar {
        return TSnackbar.make(
            anchorView,
            text,
            TSnackbar.LENGTH_SHORT
        ).apply {
            val textView: TextView =
                view.findViewById<View>(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
            textView.setTextColor(getSnackBarTextColor())
            view.setBackgroundColor(getSnackBarBackgroundColor())
        }
    }

    /**
     * material Snackbar 생성
     */
    private fun makeSnackBar(anchorView: View, text: String): Snackbar {
        return Snackbar.make(
            anchorView,
            text,
            Snackbar.LENGTH_SHORT
        ).apply {
            setTextColor(getSnackBarTextColor())
            setActionTextColor(getSnackBarActionTextColor())
            setBackgroundTint(getSnackBarBackgroundColor())
        }
    }

    protected fun showSnackbar(
        resId: Int = 0,
        appendString: String = "",
        displayCallback: BaseSnackBarCallback? = null
    ) {
        val displayText = if (resId != 0) {
            if (appendString.isNotEmpty()) {
                getString(resId, appendString)
            } else {
                getString(resId)
            }
        } else {
            appendString
        }

        this.showSnackbar(
            text = displayText,
            displayCallback = displayCallback
        )
    }

    /**
     * 스넥바 제거
     */
    protected fun closeSnackBar() {
        this.snackbar?.let {
            when (it) {
                is TSnackbar -> {
                    if (it.isShown) {
                        it.dismiss()
                    }
                }
                is Snackbar -> {
                    if (it.isShown) {
                        it.dismiss()
                    }
                }
            }
        }
    }

    protected fun isShownSnackbar(): Boolean {
        return this.snackbar?.let {
            when (it) {
                is TSnackbar -> {
                    it.isShown
                }
                is Snackbar -> {
                    it.isShown
                }
                else -> {
                    null
                }
            }
        } ?: false
    }

    /**
     * 버튼있는 스넥바
     */
    protected fun showSnackbar(
        text: String,
        @StringRes _btnTextId: Int = R.string.btn_close,
        _clickListener: View.OnClickListener? = null,
        displayCallback: BaseSnackBarCallback? = null
    ) {
        this.contentBinding.let { contentBinding ->
            var combinedBtnTextId = _btnTextId
            var combinedCallback: View.OnClickListener? = _clickListener

            val isShowDialog = this.dialog?.isShowing == true
            val parentView = if (isShowDialog) {
                this.dialog?.window?.decorView
            } else {
                contentBinding.root
            }
            parentView?.let {  rootView ->
                this.snackbar = if (isShowDialog) {
                    this.makeSnackBar(rootView, text)
                } else {
                    if (showKeyboard) {
                        this.makeTSnackBar(rootView, text)
                    } else {
                        this.makeSnackBar(rootView, text)
                    }
                }
            }
            this.snackbarText = text
            this.snackbar?.let {
                when (it) {
                    is TSnackbar -> {
                        if (combinedCallback != null) {
                            it.setAction(combinedBtnTextId, combinedCallback)
                        } else {
                            it.setAction(combinedBtnTextId) { view ->
                                it.dismiss()
                            }
                        }
                        if (displayCallback != null) {
                            it.setCallback(displayCallback.tsbCallback)
                        }
                        it.setActionTextColor(getSnackBarActionTextColor())
                        it.show()
                    }
                    is Snackbar -> {
                        if (combinedCallback != null) {
                            it.setAction(combinedBtnTextId, combinedCallback)
                        } else {
                            it.setAction(combinedBtnTextId) { view ->
                                it.dismiss()
                            }
                        }
                        if (displayCallback != null) {
                            it.addCallback(displayCallback.sbCallback)
                        }
                        it.show()
                    }
                    else -> {
                    }
                }
            }
        }
    }
}