package com.my.githubtestapplication.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.provider.Settings
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import java.io.Serializable

/**
 * 앱 내에 전역으로 사용할 기능 함수 객체
 */
object AppUtil {
    /**
     * 앱 재시작 BroadCast Send
     */
//    fun restartApplication(context : Context, tokenRefreshManager : TokenRefreshManager) {
//        tokenRefreshManager.close()
//        val intent = Intent().apply {
//            action = ConstValue.ACTIVITY_FINISH
//        }
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
//
//        this.startSplashActivity(context)
//    }

    /**
     * 카카오 맵
     */
    // 다음 지도 사용을 대비해서 코드 남겨둠 (2022.1.13)
//        fun gotoKakaoMapActivity(
//            activity: Activity,
//            requestCode: Int? = null,
//            putExtra: HashMap<String, Any>? = null
//        ) {
//            // 카카오 맵은 아래 해당 라이브러리만 지원함.
//            val array = ArrayList<String>(0)
//            Build.SUPPORTED_ABIS.forEach {
//                if (it != "arm64-v8a" &&
//                    it != "arm64-armeabi" &&
//                    it != "armeabi-v7a" &&
//                    it != "armeabi"
//                ) {
//                    array.add(it)
//                }
//            }
//
//            if (array.size > 0) {
//                ToastShowReceiver.sendAction(
//                    activity,
//                    activity.getString(R.string.kakao_map_not_working),
//                    Toast.LENGTH_SHORT
//                ).show()
//                return
//            }
//
//            this.startActivity(
//                activity,
//                makeIntent(activity, ProductMapActivity::class.java, putExtra),
//                requestCode
//            )
//        }

    /**
     * 메인화면 - 하위 페이지, 하위 탭 이동
     */
//    fun goMainActivity(
//        activity: Activity,
//        menu: Int? = null,
//        tabPosition: Int? = null,
//        _action: String = ConstValue.CHANGE_MENU_ACTION,
//        _data: Uri? = null,
//        _subTabPosition: Int? = null,
//        _showLogin: Boolean = false,
//    ) {
//        val intent = Intent(activity, MainActivity::class.java).apply {
//            action = _action
//            _data?.let{
//                data = it
//            }
//            menu?.let{
//                putExtra(ConstValue.CHANGE_MAIN_MENU, it)
//            }
//            tabPosition?.let{
//                putExtra(ConstValue.TAP_POSITION, it)
//            }
//            _subTabPosition?.let{
//                putExtra(ConstValue.SUB_TAB_POSITION, it)
//            }
//            putExtra(ConstValue.SHOW_LOGIN, _showLogin)
//            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        }
//        this.startActivity(activity, intent)
//        val gaExtra = hashMapOf(ConstValue.TAP_POSITION to tabPosition) as HashMap<String, Any>
//        AppAnalytics.sendGAScreenName(gaExtra, GoogleAnalyticsEvent.MAIN_SCREEN)
//    }

    /**
     * 앱 권한 설정 화면으로 이동
     */
    fun gotoSettingForPermission(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        this.startActivity(context, intent)
//        AppAnalytics.sendGAScreenName(null, GoogleAnalyticsEvent.APP_PERMISSION_SETTING)
    }

    /**
     * 인텐트 생성
     */
    private fun makeIntent(
        activity: Activity,
        cls: Class<*>,
        putExtra: HashMap<String, Any>? = null
    ): Intent {
        val intent = Intent(activity, cls)
        putExtra?.let {
            it.keys.forEach { keyName ->
                when (it[keyName]) {
                    is String -> {
                        intent.putExtra(keyName, it[keyName] as String)
                    }
                    is Boolean -> {
                        intent.putExtra(keyName, it[keyName] as Boolean)
                    }
                    is Float -> {
                        intent.putExtra(keyName, it[keyName] as Float)
                    }
                    is Double -> {
                        intent.putExtra(keyName, it[keyName] as Double)
                    }
                    is Int -> {
                        intent.putExtra(keyName, it[keyName] as Int)
                    }
                    is Long -> {
                        intent.putExtra(keyName, it[keyName] as Long)
                    }
                    is Serializable -> {
                        intent.putExtra(keyName, it[keyName] as Serializable)
                    }
                    is Parcelable -> {
                        intent.putExtra(keyName, it[keyName] as Parcelable)
                    }
                }
            }
        }
        return intent
    }

    /**
     * 인텐트 생성
     */
    fun makeIntent(
        context: Context,
        cls: Class<*>,
        putExtra: HashMap<String, Any>? = null
    ): Intent {
        val intent = Intent(context, cls)
        putExtra?.let {
            it.keys.forEach { keyName ->
                when (it[keyName]) {
                    is String -> {
                        intent.putExtra(keyName, it[keyName] as String)
                    }
                    is Boolean -> {
                        intent.putExtra(keyName, it[keyName] as Boolean)
                    }
                    is Float -> {
                        intent.putExtra(keyName, it[keyName] as Float)
                    }
                    is Double -> {
                        intent.putExtra(keyName, it[keyName] as Double)
                    }
                    is Int -> {
                        intent.putExtra(keyName, it[keyName] as Int)
                    }
                    is Long -> {
                        intent.putExtra(keyName, it[keyName] as Long)
                    }
                    is Serializable -> {
                        intent.putExtra(keyName, it[keyName] as Serializable)
                    }
                    is Parcelable -> {
                        intent.putExtra(keyName, it[keyName] as Parcelable)
                    }
                }
            }
        }
        return intent
    }

    /**
     * request code 유무에 따라 엑티비티 실행
     */
    private fun startActivity(
        activityOrContext: Any, intent: Intent, requestCode: Int? = null,
        option: ActivityOptionsCompat? = null
    ) {
        if (activityOrContext is Activity) {
            if (requestCode != null) {
                if (option != null) {
                    activityOrContext.startActivityForResult(
                        intent,
                        requestCode,
                        option.toBundle()
                    )
                } else {
                    activityOrContext.startActivityForResult(intent, requestCode)
                }
            } else {
                if (option != null) {
                    activityOrContext.startActivity(intent, option.toBundle())
                } else {
                    activityOrContext.startActivity(intent)
                }
            }
        } else if (activityOrContext is Fragment) {
            if (requestCode != null) {
                if (option != null) {
                    activityOrContext.startActivityForResult(
                        intent,
                        requestCode,
                        option.toBundle()
                    )
                } else {
                    activityOrContext.startActivityForResult(intent, requestCode)
                }
            } else {
                if (option != null) {
                    activityOrContext.startActivity(intent, option.toBundle())
                } else {
                    activityOrContext.startActivity(intent)
                }
            }
        } else if (activityOrContext is Context) {
            if (option != null) {
                activityOrContext.startActivity(intent, option.toBundle())
            } else {
                activityOrContext.startActivity(intent)
            }
        }
    }
}