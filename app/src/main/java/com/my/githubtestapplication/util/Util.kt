package com.my.githubtestapplication.util

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.text.style.RelativeSizeSpan
import android.util.Size
import android.util.TypedValue
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.my.githubtestapplication.BuildConfig
import com.my.githubtestapplication.GithubApplication
import com.my.githubtestapplication.R
import com.my.githubtestapplication.util.ConstValue
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.SingleSubject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object Util {
    val TAG = Util::class.java.simpleName

    fun ratioSize(activity : Activity, width : Float, height : Float) : Point {
        return Util.ratioSize(activity.baseContext, width, height)
    }

    /**
     * Width ???????????? height??? scale ?????? ??????
     * @param context
     * @param width ????????? ???????????? ???
     * @param height ????????? ???????????? ???
     * @return ???????????? ??????
     */
    fun ratioSize(context : Context, width : Float, height : Float) : Point {
        val scaledPoint = Point(0, 0)
        try {
            val point = Util.windowSize(context)
            val scale = point.x / width
            val scaleHeight = scale * height
            scaledPoint.x = point.x
            scaledPoint.y = scaleHeight.toInt()
        } catch (e : Exception) {
            Log.e(TAG, String.format("ratioSize() %s", e.message))
        }
        return scaledPoint
    }

    /**
     * width, height scale ?????? ??????
     * @param context
     * @param designSize ?????? ????????? ?????? ????????? ????????? ?????? ??????
     * @param guideSize ????????? ??? ??????
     * @return
     */
    fun ratioSize(
        context : Context,
        designSize : Size,
        guideSize : Size
    ) : Size {
        var scaledSize = Size(0, 0)
        try {
            val point = Util.windowSize(context)
            val widthScale = guideSize.width * (point.x / designSize.width)
            val heightScale = guideSize.height * (point.y / designSize.height)
            scaledSize = Size(widthScale, heightScale)
        } catch (e : Exception) {
            Log.e(TAG, String.format("ratioSize() %s", e.message))
        }
        return scaledSize
    }

    /**
     * ????????? ???????????? ???????????? ??? ??????
     * @param context
     * @param dpDesignSize dp ?????? ????????? ?????? ?????????
     * @param dpGuideSize dp ?????? ????????? ?????????
     * @return
     */
    fun ratioSize(
        context : Context,
        dpDesignSize : Int,
        dpGuideSize : Int
    ) : Float {
        var pixelGuideSize = pxFromDp(context, dpGuideSize.toFloat())
        try {
            val point = Util.windowSize(context)
            val pixelDesignSize = pxFromDp(context, dpDesignSize.toFloat())
            pixelGuideSize *= (point.x / pixelDesignSize)
        } catch (e : Exception) {
            Log.e(TAG, String.format("ratioSize() %s", e.message))
        }
        return pixelGuideSize
    }

    /**
     * ???????????? ?????? ????????? ????????? ????????? ?????? ??????
     * @param displayWidth
     * @param designedWidth
     * @param designedHeight
     * @return
     */
    fun ratioSize(
        displayWidth : Float,
        designedWidth : Float,
        designedHeight : Float
    ) : Float {
        var scaleHeight = 0f
        try {
            val scaleSize = displayWidth / designedWidth
            scaleHeight = scaleSize * designedHeight
        } catch (e : Exception) {
            Log.e(TAG, String.format("ratioSize() %s", e.message))
        }

        return scaleHeight
    }

    fun windowSize(context : Context?) : Point {
        val point = Point()
        context?.let { ctx ->
            val surfaceRotation = getScreenOrientation(ctx)
            val softNavigationHeight = navigationHeight(ctx)
            val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val bounce = wm.currentWindowMetrics.bounds
                point.set(bounce.right - bounce.left, bounce.bottom - bounce.top)
            } else {
                val display = wm.defaultDisplay
                display.getRealSize(point)
            }

            when (surfaceRotation) {
                Surface.ROTATION_180,
                Surface.ROTATION_0 -> {
                    point.y = point.y - softNavigationHeight
                }
                Surface.ROTATION_270,
                Surface.ROTATION_90 -> {
                    point.x = point.x - softNavigationHeight
                }
            }
        }

        return point
    }

    /**
     * ?????? rotation ????????? ??????
     * @param context
     * @return Surface.ROTATION_0 ...
     */
    fun getScreenOrientation(context : Context) : Int {
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display
        } else {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        }
        return display?.rotation ?: Surface.ROTATION_0
    }

    /**
     * soft navigation bar Height
     * @param context
     * @return int pixel value
     */
    fun navigationHeight(context : Context) : Int {
        var navigationBarHeight = 0
        val resourceId = context.resources.getIdentifier(
            "navigation_bar_height",
            "dimen",
            "android"
        )
        if (resourceId > 0) {
            navigationBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        return navigationBarHeight
    }

    /**
     * status bar Height
     * @param context
     * @return int pixel value
     */
    fun statusBarHeight(context : Context?) : Int {
        var result = 0
        context?.let { ctx ->
            val resourceId = ctx.resources.getIdentifier(
                "status_bar_height",
                "dimen",
                "android"
            )
            if (resourceId > 0) {
                result = ctx.resources.getDimensionPixelSize(resourceId)
            }
        }
        return result
    }

    /**
     * pixel ?????? ?????? ????????? density??? ????????????
     * @param context
     * @param px pixel size
     * @return dp value
     */
    fun dpFromPx(context : Context, px : Float) : Float {
        return px / context.resources.displayMetrics.density
    }

    /**
     * dp?????? pixel????????? ?????? ?????????.
     * @param context
     * @param dp
     * @return
     */
    fun pxFromDp(context : Context, dp : Float) : Float {
        return dp * context.resources.displayMetrics.density
    }

    /**
     * sp?????? pixel????????? ?????? ?????????.
     * @param context
     * @param sp
     * @return
     */
    fun pxFromSp(context : Context, sp : Float) : Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        )
    }

    /**
     * format??? ????????? ??????
     * @param format: format String
     * @param date: calendar Date
     * @return date format string
     */
    fun simpleDateFormat(format : String, date : Date) : String {
        return SimpleDateFormat(format, Locale.ENGLISH).format(date)
    }

    /**
     * ????????? date??? ??????
     * @param format: format String
     * @param date: utc date string
     * @return gmt date
     */
    fun parseSimpleDateFormat(format : String, date : String) : Date {
        return try {
            val df = SimpleDateFormat(format, Locale.ENGLISH)
            df.timeZone = TimeZone.getTimeZone("UTC")
            df.parse(date) ?: throw NullPointerException("null date")
        } catch (e : Exception) {
            Date()
        }
    }

    /**
     * text??? format ???????????? newFormat ????????? ??????
     * @param text: string
     * @param _format: format String
     * @param _newFormat: new format String
     * @return newDateString
     */
    fun convertSimpleDateFormat(text : String, _format : String, _newFormat : String) : String {
        return try {
            val localZone = TimeZone.getTimeZone("UTC")
            val format = SimpleDateFormat(_format, Locale.ENGLISH).apply {
                timeZone = localZone
            }
            val newFormat = SimpleDateFormat(_newFormat, Locale.ENGLISH).apply {
                timeZone = localZone
            }
            format.parse(text)?.let {
                newFormat.format(it)
            } ?: text
        } catch (e : ParseException) {
            text
        }
    }

    /**
     * ????????? ampm hh:mm ??? ??????
     * @param format: format String
     * @param date: utc date string
     * @return gmt date
     */
    fun convertDateFormatAPMHHMM(am : String, pm : String, date : Date?) : String {
        return date?.let {
            try {
                val calendar = Calendar.getInstance().apply { time = date }
                val amPm = calendar.get(Calendar.AM_PM)
                var hour = String.format("%02d", calendar.get(Calendar.HOUR))
                val minute = String.format("%02d", calendar.get(Calendar.MINUTE))

                if (amPm == Calendar.AM) {
                    "$am $hour:$minute"
                } else {
                    //?????? 00??? ??? ?????? 12?????? ??????
                    if (hour == "00") {
                        hour = "12"
                    }
                    "$pm $hour:$minute"
                }
            } catch (e : ParseException) {
                ""
            }
        } ?: ""
    }

    /**
     * ?????? ?????? ??????????????? ?????????.
     * @param date: date object
     * @return DateType
     */
//    fun diffDate(date: Date): DateType {
//        val todayYear = Util.getCalender().get(Calendar.YEAR)
//        val todayDay = Util.getCalender().get(Calendar.DAY_OF_YEAR)
//
//        val target = Util.getCalender()
//        target.time = date
//        val targetYear = target.get(Calendar.YEAR)
//        val targetDay = target.get(Calendar.DAY_OF_YEAR)
//
//        return if (todayYear == targetYear) {
//            //?????? ?????? ?????? ??????
//            if (todayDay != targetDay) {
//                if (todayDay == (targetDay + 1)) {
//                    //??????
//                    DateType.YESTERDAY
//                } else {
//                    //????????????
//                    DateType.OTHER
//                }
//            } else {
//                //??????
//                DateType.TODAY
//            }
//        } else if (todayDay == 1 && targetDay == 365) {
//            // 12??? 31??? ??? 1??? 1??? ??????
//            DateType.YESTERDAY
//        } else {
//            DateType.OTHER
//        }
//    }

    /**
     * visible ????????? ???????????? ????????? ????????? ???????????? ??????
     * @param view
     * @param newVisible
     */
    fun setCheckVisible(view : View, newVisible : Int) {
        val currentVisible = view.visibility
        if (currentVisible != newVisible) {
            view.visibility = newVisible
        }
    }

    /**
     * keyboard show
     * @param context
     * @param view activity.currentFocus
     */
    fun showKeyboard(context : Context?, view : View?) {
        if (context != null && view != null) {
            val inputManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    /**
     * keyboard close
     * @param context
     * @param view activity.currentFocus
     */
    fun hideKeyboard(context : Context?, view : View?) {
        if (context != null && view != null) {
            val inputManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * ????????? format
     * @param format format ex) "%.1f"
     * @param num float number
     */
    fun setFloatFormat(format : String?, num : Float) : String? {
        return try {
            String.format(format!!, num)
        } catch (e : Exception) {
            Log.e(TAG, "setFloatFormat Exception : ", e)
            ""
        }
    }

    /**
     * view visible ??????
     */
    fun setVisible(view : View?, isVisible : Boolean) {
        view?.let {
            var newVisible = View.GONE
            if (isVisible) {
                newVisible = View.VISIBLE
            }
            if (it.visibility != newVisible) {
                it.visibility = newVisible
            }
        }
    }

    /**
     * view enable ??????
     */
    fun setEnable(view : View?, isEnable : Boolean) {
        view?.let {
            if (it.isEnabled != isEnable) {
                it.isEnabled = isEnable
            }
        }
    }

    /**
     * getColor
     */
    fun getColor(context : Context, @ColorRes color : Int) : Int {
        return context.resources.getColor(color, context.theme)
    }

    /**
     * getDrawable
     */
    fun getDrawable(
        context : Context, @DrawableRes drawable : Int
    ) : Drawable? {
        return ResourcesCompat.getDrawable(context.resources, drawable, null)
    }

    /**
     * Drawable -> Bitmap
     */
    fun drawableToBitmap(drawable : Drawable) : Bitmap? {
        var bitmap : Bitmap? = null
        try {
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }

            bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        } catch (e : Exception) {
            Log.e(TAG, "drawableToBitmap Exception : ", e)
        }
        return null
    }

    /**
     * drawableLeft...?????? tint color ???????????? ??????
     * @param textView
     * @param color
     */
    fun setTintColor(textView : TextView, @ColorRes color : Int) {
        for (drawable in textView.compoundDrawablesRelative) {
            drawable?.let {
                it.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(textView.context, color),
                    PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    /**
     * ???????????? ?????? ??????
     * @param context
     * @param designSize
     * @param guideSize
     * @return
     */
    fun ratioQuadRateSize(
        context : Context,
        designSize : Int,
        guideSize : Int
    ) : Size {
        var scaledSize = Size(0, 0)
        try {
            val point = windowSize(context)
            val widthScale = guideSize * (point.x.toFloat() / designSize)
            scaledSize = Size(widthScale.toInt(), widthScale.toInt())
        } catch (e : Exception) {
            Log.e(TAG, String.format("ratioSize() %s", e.message))
        }
        return scaledSize
    }

    /**
     * ????????? TextView??? ?????? ????????? ????????? ?????? ?????? ??????
     * @param text ?????? ?????????
     * @param afterChar ?????? ?????? ?????? ?????? ??????
     * @param reduceBy scaleSize
     * @return
     */
    fun spannableStringBuilder(
        text : String,
        afterChar : Char,
        reduceBy : Float
    ) : SpannableStringBuilder {
        val smallSizeText = RelativeSizeSpan(reduceBy)
        val ssBuilder = SpannableStringBuilder(text)
        ssBuilder.setSpan(
            smallSizeText,
            text.indexOf(afterChar),
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return ssBuilder
    }

    /**
     * image with Text not set drawable
     */
    fun setImageSpannable(
        context : Context,
        tv : TextView,
        text : String,
        drawableResourceId : Int,
        startPoint : Int = 0,
        verticalAlignment : Int = ImageSpan.ALIGN_BASELINE
    ) {
        ContextCompat.getDrawable(context, drawableResourceId)?.let {
            this.setImageSpannable(
                tv,
                text,
                it,
                startPoint,
                verticalAlignment
            )
        }
    }

    /**
     * image with Text not set drawable
     */
    fun setImageSpannable(
        tv : TextView,
        text : String,
        drawable : Drawable,
        startPoint : Int = 0,
        verticalAlignment : Int = ImageSpan.ALIGN_BASELINE
    ) {
        try {
            val textSize = tv.textSize
            val imageScale = drawable.intrinsicHeight / textSize
            val size = Size(
                (drawable.intrinsicWidth / imageScale).toInt(),
                textSize.toInt()
            )
            drawable.setBounds(0, 0, size.width, size.height)
            val imageSpan = ImageSpan(drawable, verticalAlignment)
            // ????????? ??????????????????.
            var addSpanText = ""
            var appendStartPoint = startPoint
            when (appendStartPoint) {
                0 -> {
                    addSpanText = "  $text"
                }
                (text.length - 1) -> {
                    addSpanText = "$text  "
                    appendStartPoint = addSpanText.length - 1
                }
                else -> {
                    val prefixText = text.subSequence(0, startPoint)
                    val postfixText = text.subSequence(startPoint, text.length)
                    addSpanText = "$prefixText   $postfixText"
                    appendStartPoint += 1
                }
            }
            val ssBuilder = SpannableStringBuilder(addSpanText)
            ssBuilder.setSpan(
                imageSpan,
                appendStartPoint,
                appendStartPoint + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tv.text = ssBuilder
        } catch (ex : Exception) {
            Log.e("Util", "setImageSpannable()", ex)
            tv.text = text
        }
    }

    /**
     * ?????? ??????
     */
    fun writeToFile(
        folderName : String,
        fileName : String,
        data : String
    ) : Boolean {
        try {
            val folder = File(folderName)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            val file = File(folderName + fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            val stream = FileOutputStream("$folderName/$fileName")
            stream.write(data.toByteArray())
            stream.close()
            return true
        } catch (e : Exception) {
            Log.e(TAG, "writeToFile File write failed: $e")
        }
        return false
    }

    /**
     * ?????? ??????
     */
    fun readToFile(folderName : String, fileName : String) : String? {
        try {
            val folder = File(folderName)
            if (!folder.exists()) {
                return null
            }
            val file = File(folderName + fileName)
            if (!file.exists()) {
                return null
            }
            val bytes = ByteArray(file.length().toInt())
            val stream =
                FileInputStream("$folderName/$fileName")
            stream.read(bytes)
            stream.close()
            return String(bytes)
        } catch (e : Exception) {
            Log.e(TAG, "writeToFile File write failed: $e")
        }
        return null
    }

    /**
     * ??? ????????? ?????? ?????? ??????
     */
    fun checkDifferentData(
        currentData : ArrayList<Int>,
        newData : ArrayList<Int>
    ) : Boolean {
        val differentItem : HashMap<Int, Int> = HashMap(0)
        for (i in currentData.indices) {
            val item = currentData[i]
            differentItem[item] = item
        }
        for (i in newData.indices) {
            val newItem = newData[i]
            if (differentItem[newItem] != null) {
                differentItem.remove(newItem)
            } else {
                differentItem[newItem] = newItem
            }
        }
        return differentItem.size > 0
    }

    /**
     * Y/N return String
     */
    fun booleanToYN(yn : Boolean) : String {
        return if (yn) "Y" else "N"
    }

    /**
     * Y/N return Boolean
     */
    fun ynToBoolean(yn : String?) : Boolean {
        return yn?.let { state ->
            state.toUpperCase(Locale.ROOT) == "Y"
        } ?: false
    }

    /**
     * GMT??? ????????? Calender
     */
    fun getCalender() = Calendar.getInstance(TimeZone.getDefault())

    /**
     * RecyclerView delete animator
     * ????????? ???????????? ??? ????????? ?????? ??????
     */
    fun deleteRecyclerAnimator(recyclerView : RecyclerView?) {
        recyclerView?.let {
            val animator = it.itemAnimator
            if (animator != null && animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
        }
    }

    /**
     * Notification State
     */
    fun getDeviceNotification(context : Context) : HashMap<String, Boolean> {
        val map = HashMap<String, Boolean>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val listNotificationChannel =
                notificationManager.notificationChannels as ArrayList<NotificationChannel>

            listNotificationChannel.forEach {
                map[it.id] = it.importance != NotificationManager.IMPORTANCE_NONE
            }
        }

        return map
    }

    /**
     * soft navigation bar ??????
     */
    fun getSoftNavigationBarHeight(context : Context) : Int {
        val id : Int =
            context.resources.getIdentifier(
                "config_showNavigationBar", "bool", "android"
            )
        if (context.resources.getBoolean(id)) {
            val resourceIdBottom : Int =
                context.resources.getIdentifier(
                    "navigation_bar_height", "dimen", "android"
                )
            if (resourceIdBottom > 0) {
                return context.resources.getDimensionPixelSize(resourceIdBottom)
            }
        }
        return 0
    }

    fun <T> getDebounceObservable(
        disposable : CompositeDisposable,
        delay : Long,
        callback : (T) -> Unit
    ) : PublishSubject<T> {
        return getInnerDelayObservable(disposable, delay, callback, DelayType.Debounce)
    }

    fun <T> getThrottleFirstObservable(
        disposable : CompositeDisposable,
        delay : Long,
        callback : (T) -> Unit
    ) : PublishSubject<T> {
        return getInnerDelayObservable(disposable, delay, callback, DelayType.ThrottleFirst)
    }

    private enum class DelayType {
        ThrottleFirst,
        Debounce
    }

    private fun <T> getInnerDelayObservable(
        disposable : CompositeDisposable,
        delay : Long,
        callback : (T) -> Unit,
        delayType : DelayType
    ) : PublishSubject<T> {
        var publishSubject : PublishSubject<T> = PublishSubject.create<T>()
        val observable = when (delayType) {
            DelayType.ThrottleFirst -> publishSubject.throttleFirst(
                delay,
                TimeUnit.MILLISECONDS
            )
            DelayType.Debounce -> publishSubject.debounce(delay, TimeUnit.MILLISECONDS)
        }
        observable.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<T> {
                override fun onSubscribe(d : Disposable?) {
                    disposable.add(d)
                }

                override fun onNext(t : T?) {
                    t?.let {
                        callback(t)
                    }
                }

                override fun onError(e : Throwable?) {
                }

                override fun onComplete() {
                }
            })

        return publishSubject
    }

    fun getLanguageCode() : String {
        val locale = Locale.getDefault()
        // TODO ????????? ????????? ?????? ???????????? ????????? LanguageCode??? ko_KR ?????? ??????
//            return "${locale.language}_${locale.country}"
        return "ko_KR"
    }

    fun getSSAID(context : Context) : String {
        return Settings.Secure.getString(
            context.applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    /**
     * uri query Param?????? getQueryParameter() ???????????? ?????? ????????? ???????????? ????????? ?????????
     * getQueryParameters() ???????????? ?????? ????????? ???????????? return ??? array?????? wraping ????????? ?????????.
     */
    fun getFirstQueryParameters(uri : Uri, key : String) : String? {
        var returnString : String? = null
        try {
            val list = uri.getQueryParameters(key)
            if (list.isNotEmpty()) {
                returnString = list[0]
            }
        } catch (e : Exception) {
            Log.e("Util", "getFirstQueryParameters()", e)
        }
        return returnString
    }

    /**
     * View Alpha Change (A -> B ??? ???????????????, A -> 0(transparent) -> B) : -1 ~ 1
     * A -> 0 ????????? ????????? ??????
     * 0 -> B ????????? ????????? ??????
     */
    fun convertTitleBarAlphaRange(baseline : Float, absOffset : Float) : Float {
        val alpha : Float
        var offset = absOffset
        if (offset <= baseline) {
            alpha = 1 - (offset / baseline)
        } else {
            offset -= baseline
            alpha = (offset / baseline) * -1
        }

        return alpha
    }

    /**
     * Emoji return
     */
    fun getEmojiByUnicode(unicode : Int) : String {
        return String(Character.toChars(unicode))
    }

    /**
     * ??????, ??????????????? ????????? ??????
     */
    fun validCount(value : Long, max : Long, min : Long) : Long {
        return Math.min(max, Math.max(value, min))
    }

    /**
     * ??????, ??????????????? ????????? ??????
     */
    fun validCount(value : Int, max : Int, min : Int) : Int {
        return validCount(value.toLong(), max.toLong(), min.toLong()).toInt()
    }

    /**
     * ??????, ??????????????? ????????? ??????
     */
    fun validCount(value : Float, max : Float, min : Float) : Float {
        return validCount(value.toDouble(), max.toDouble(), min.toDouble()).toFloat()
    }

    /**
     * ??????, ??????????????? ????????? ??????
     */
    fun validCount(value : Double, max : Double, min : Double) : Double {
        return Math.min(max, Math.max(value, min))
    }

    /**
     * view ?????? ???????????????
     */
    fun rotateAnimation(
        objectAnimator : ObjectAnimator?,
        view : View,
        fromDegree : Float,
        toDegree : Float,
        duration : Long
    ) : ObjectAnimator {
        var realObjectAnimator = objectAnimator
        realObjectAnimator?.cancel()
        realObjectAnimator = ObjectAnimator.ofFloat(view, View.ROTATION, fromDegree, toDegree)
        realObjectAnimator.duration = duration
        realObjectAnimator.start()
        return realObjectAnimator
    }

    /**
     * view height ???????????????
     */
    fun valueAnimation(
        valueAnimator : ValueAnimator?,
        fromValue : Int,
        toValue : Int,
        duration : Long,
        animatorUpdateListener : ValueAnimator.AnimatorUpdateListener
    ) : ValueAnimator {
        var realValueAnimator = valueAnimator
        realValueAnimator?.cancel()
        realValueAnimator = ValueAnimator.ofInt(fromValue, toValue)
        realValueAnimator.duration = duration
        realValueAnimator.addUpdateListener(animatorUpdateListener)
        realValueAnimator.start()
        return realValueAnimator
    }

    /**
     * list item state scale animation
     */
    fun setStateListAnimator(view : View?, context : Context) {
        view?.let {
            it.stateListAnimator = AnimatorInflater.loadStateListAnimator(
                context,
                R.drawable.list_animation
            )
        }
    }

    /**
     * bit value ??????
     */
    fun setBitValue(originValue : Int, setBit : Int) = originValue or setBit

    /**
     * bit value ??????
     */
    fun unSetBitValue(originValue : Int, setBit : Int) = originValue and setBit.inv()

    /**
     * ????????? ???????????? ????????? ??????
     */
//    fun getShareLinkMessage(context : Context, _prefixText : String, shortLink : String) : String {
//        var prefixText = _prefixText
//        if (prefixText.length > 20) {
//            "${prefixText.substring(0, 20)}...".also { prefixText = it }
//        }
//        return context.resources.getString(
//            R.string.share_content_format,
//            prefixText,
//            context.resources.getString(R.string.app_name_kor),
//            shortLink
//        )
//    }

    fun getSpToPixel(context : Context, spSize : Float) : Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            spSize,
            context.getResources().getDisplayMetrics()
        )
    }

    /**
     * ??? ??????
     */
    fun getAppVersion(
        context : Context
    ) : String {
        val versionPostFix = GithubApplication.getBuildType()
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        }
        return when (BuildConfig.BUILD_TYPE) {
            ConstValue.RELEASE -> {
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
            }
            else -> {
                "${context.packageManager.getPackageInfo(context.packageName, 0).versionName} $versionPostFix $versionCode"
            }
        }
    }

    /**
     * ?????? ?????? ??????
     * @param lastVer ?????? ??????
     * @param lastVerNm ?????? ??????
     */
    fun checkLastAppVersion(lastVer : Long, lastVerNm : String) : Boolean {
        return BuildConfig.VERSION_CODE < lastVer ||
                versionStringCompare(BuildConfig.VERSION_NAME, lastVerNm)
    }

    /**
     * ????????? ???????????? ???????????? ?????? ?????? ??????
     * return T: ??????????????? ????????????. F: ???????????????.
     */
    private fun versionStringCompare(appVersionName : String, compareVersionName : String) : Boolean {
        try {
            val appVersionArray = appVersionName.split(".")
            val compareVersionArray = compareVersionName.split(".")
            var retValue = false
            for (i in appVersionArray.indices) {
                val appNumber = appVersionArray[i].toInt()
                val compareNumber = compareVersionArray[i].toInt()
                if (appNumber < compareNumber) {
                    retValue = true
                    break
                }
            }
            return retValue
        } catch (e : Exception) {
            Log.e(TAG, "checkAppVersion()", e)
        }
        return false
    }

    /**
     * ?????? ??????
     */
    fun gotoMarket(activity : Activity) {
        val uri = "market://details?id=${activity.packageName}"
        Log.i(TAG, "gotoMarket() uri: $uri")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.data = Uri.parse(uri)
        activity.startActivity(intent)
    }
}