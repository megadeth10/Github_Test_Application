package com.my.githubtestapplication.securepreference

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.my.githubtestapplication.securepreference.callback.SecureStoreCallback
import com.my.githubtestapplication.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import javax.inject.Inject

class SecureSharedPreferences @Inject constructor(@ApplicationContext context : Context) {
    private val tagName = SecureSharedPreferences::class.simpleName
    private var sharedPreferences : SharedPreferences

    init {
        this.sharedPreferences = this.getPreference(context)
    }

    private fun getPreference(context : Context) : SharedPreferences {
        val masterKeyAlias : String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
            "secret_shared_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        // use the shared preferences and editor as you normally would
    }

    /**
     * @param key : store key name
     * @param value : store value
     * @param callback : Async thread to end callback(필수는 아님)
     */
    suspend fun storeString(key : String, value : String, callback : SecureStoreCallback?) {
        withContext(Dispatchers.IO) {
            val time = Calendar.getInstance().timeInMillis
            val preferenceObject = PreferencesObject(key, value, callback)
            val editor = sharedPreferences.edit()
            editor.putString(preferenceObject.key, preferenceObject.value as String)
            editor.apply()
            preferenceObject.function?.let {
                Log.e(tagName, "SecurePreferences.setValue() finish")
                it.storeFinish()
            }
            Log.e(
                tagName, String.format(
                    "set() consumed time: %d",
                    Calendar.getInstance().timeInMillis - time
                )
            )
        }
    }

    suspend fun storeInt(key : String, value : Int, callback : SecureStoreCallback?) {
        withContext(Dispatchers.IO) {
            val preferenceObject = PreferencesObject(key, value, callback)
            val editor = sharedPreferences.edit()
            editor.putInt(preferenceObject.key, preferenceObject.value as Int)
            editor.apply()
            preferenceObject.function?.let {
                Log.e(tagName, "storeInt() finish")
                it.storeFinish()
            }
        }
    }

    suspend fun storeLong(key : String, value : Long, callback : SecureStoreCallback?) {
        withContext(Dispatchers.IO) {
            val preferenceObject = PreferencesObject(key, value, callback)
            val editor = sharedPreferences.edit()
            editor.putLong(preferenceObject.key, preferenceObject.value as Long)
            editor.apply()
            preferenceObject.function?.let {
                Log.e(tagName, "storeLong() finish")
                it.storeFinish()
            }
        }
    }

    suspend fun storeFloat(key : String, value : Float, callback : SecureStoreCallback?) {
        withContext(Dispatchers.IO) {
            val preferenceObject = PreferencesObject(key, value, callback)
            val editor = sharedPreferences.edit()
            editor.putFloat(preferenceObject.key, preferenceObject.value as Float)
            editor.apply()
            preferenceObject.function?.let {
                Log.e(tagName, "storeFloat() finish")
                it.storeFinish()
            }
        }
    }

    suspend fun storeBoolean(
        key : String,
        value : Boolean,
        callback : SecureStoreCallback?
    ) {
        withContext(Dispatchers.IO) {
            val preferenceObject = PreferencesObject(key, value, callback)
            val editor = sharedPreferences.edit()
            editor.putBoolean(preferenceObject.key, preferenceObject.value as Boolean)
            editor.apply()
            preferenceObject.function?.let {
                Log.e(tagName, "storeBoolean() finish")
                it.storeFinish()
            }
        }
    }

    suspend fun <T> storeArrayList(
        key : String,
        value : ArrayList<T>,
        callback : SecureStoreCallback?
    ) {
        withContext(Dispatchers.IO) {
            Log.e(tagName, "storeArrayList()")
            val time = Calendar.getInstance().timeInMillis
            val preferenceObject = PreferencesObject(key, value, callback)
            val editor = sharedPreferences.edit()
            val jsonArray = JSONArray()
            val values = preferenceObject.value as ArrayList<T>
            values.forEach {
                jsonArray.put(it)
            }

            if (jsonArray.length() > 0) {
                editor.putString(preferenceObject.key, jsonArray.toString())
            } else {
                editor.remove(preferenceObject.key)
            }
            editor.apply()
            preferenceObject.function?.let {
                Log.e(tagName, "storeArrayList() finish")
                it.storeFinish()
            }

            Log.e(
                tagName, String.format(
                    "storeArrayList() consumed time: %d",
                    Calendar.getInstance().timeInMillis - time
                )
            )
        }
    }

    fun getString(key : String, defaultValue : String) : String {
        return this.sharedPreferences.getString(key, defaultValue)!!
    }

    fun getBoolean(key : String, defaultValue : Boolean) : Boolean {
        return this.sharedPreferences.getBoolean(key, defaultValue)
    }

    fun getFloat(key : String, defaultValue : Float) : Float {
        return this.sharedPreferences.getFloat(key, defaultValue)
    }

    fun getInt(key : String, defaultValue : Int) : Int {
        return this.sharedPreferences.getInt(key, defaultValue).toInt()
    }

    fun getLong(key : String, defaultValue : Long) : Long {
        return this.sharedPreferences.getLong(key, defaultValue).toLong()
    }

    fun <T> getArrayList(key : String) : ArrayList<T> {
        val stringValue = getString(key, "")
        if (stringValue != "") {
            try {
                val returnArray = ArrayList<T>()

                val jsonArray = JSONArray(stringValue)
                for (jsonIdx in 0 until jsonArray.length()) {
                    returnArray.add(jsonArray.opt(jsonIdx) as T)
                }

                return returnArray
            } catch (e : JSONException) {

            }
        }

        return ArrayList<T>()
    }

    /**
     * preference 해당 데이터 삭제
     */
    fun remove(key : String) {
        val time = Calendar.getInstance().timeInMillis
        val preferences = this.sharedPreferences.edit()
        preferences.remove(key)
        preferences.apply()
        Log.e(
            tagName, String.format(
                "remove() consumed time: %d",
                Calendar.getInstance().timeInMillis - time
            )
        )
    }

    private class PreferencesObject(
        var key : String,
        var value : Any,
        var function : SecureStoreCallback?
    )
}