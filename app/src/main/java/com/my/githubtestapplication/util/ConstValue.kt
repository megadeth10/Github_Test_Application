package com.my.githubtestapplication.util

/**
 * 전역으로 사용하려는 상수 객체
 */
object ConstValue {
    // Activity Request code는 중복 방지를 위해 아래와 같이 이전 코드 값에 1을 더한 값으로 사용한다.
    const val ACTIVITY_REQUEST_CODE = 1

    // Android request permission code
    const val CAMERA_EXTERNAL_PERMISSION_CODE = 1
    const val LOCATION_PERMISSION_CODE = CAMERA_EXTERNAL_PERMISSION_CODE + 1
    const val STORAGE_PERMISSION_CODE = LOCATION_PERMISSION_CODE + 1

    const val BACK_PRESS_DELAY_MILLISECOND: Long = 200
    const val TRANSITION_DURATION_MILLISECOND: Long = 200
    const val NOTIFICATION_DIALOG_MILLISECOND: Long = 5000

    const val APP_FOLDER_NAME = "deleo"

    const val MILLISECOND = 1000
    const val MINUTE_IN_MILLISECOND: Long = (60 * MILLISECOND).toLong()
    const val HOUR_IN_MILLISECOND: Long = 60 * MINUTE_IN_MILLISECOND
    const val DAY_IN_MILLISECOND: Long = 24 * HOUR_IN_MILLISECOND
    const val MONTH_IN_MILLISECOND: Long = 30 * DAY_IN_MILLISECOND
    const val YEAR_IN_MILLISECOND: Long = 365 * DAY_IN_MILLISECOND

    //7.0 android.os.FileUriExposedException
    const val PROVIDER = "com.my.githubtestapplication.fileprovider"
    //7.0 android.os.FileUriExposedException

    const val DATE_DISPLAY_FORMAT = "yyyy.MM.dd."

    const val DEBUG = "debug"
    const val RELEASE = "release"
    const val SCREEN = "screen"
}