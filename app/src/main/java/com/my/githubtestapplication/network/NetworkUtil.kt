package com.my.githubtestapplication.network

import com.my.githubtestapplication.util.Log
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

/**
 * 우리 서비스 통신 단에 사용할 공통부 기능을 정의한 객체
 */
object NetworkUtil {
    /**
     * OkHttpRequest Body 만들기
     */
    fun makeRequestBody(bodyObject: Any?): RequestBody {
        val mediaType: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
        return Gson().toJson(bodyObject).toRequestBody(mediaType)
    }

    /**
     * http code 값 return
     */
    fun parseHttpErrorCode(e: Throwable): Int? {
        var code: Int? = null
        if (e is HttpException) {
            try {
                code = e.code()
            } catch (e: Exception) {
                Log.e("NetworkUtil", "parseHttpErrorCode()", e)
            }
        }
        return code
    }
}
