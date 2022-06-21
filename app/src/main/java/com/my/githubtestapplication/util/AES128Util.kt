package com.my.githubtestapplication.util

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.Key
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AES128Util(key: String, type: AES_TYPE = AES_TYPE.AES128) {
    private val iv: String
    private val keySpec: Key
    private val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private val ALGORITHM = "AES"

    // 암호화
    @Throws(
        UnsupportedEncodingException::class,
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun encode(str: String): String {
        val c = Cipher.getInstance(TRANSFORMATION)
        c.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(iv.toByteArray()))
        val encrypted = c.doFinal(str.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    //복호화
    @Throws(
        UnsupportedEncodingException::class,
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun decode(str: String): String {
        val c = Cipher.getInstance(TRANSFORMATION)
        c.init(
            Cipher.DECRYPT_MODE,
            keySpec,
            IvParameterSpec(iv.toByteArray(StandardCharsets.UTF_8))
        )
        val byteStr = Base64.decode(str.toByteArray(), 0)
        return String(c.doFinal(byteStr), StandardCharsets.UTF_8)
    }

    init {
        iv = key.substring(0, 16)
        val keyBytes = ByteArray(AES_TYPE.getValue(type))
        val b = key.toByteArray(StandardCharsets.UTF_8)
        var len = b.size
        if (len > keyBytes.size) {
            len = keyBytes.size
        }
        System.arraycopy(b, 0, keyBytes, 0, len)
        val keySpec = SecretKeySpec(keyBytes, ALGORITHM)
        this.keySpec = keySpec
    }

    companion object {
        enum class AES_TYPE(value: Int) {
            AES128(16),
            AES256(32);

            companion object {
                fun getValue(type: AES_TYPE): Int {
                    return when (type) {
                        AES128 -> {
                            16
                        }
                        else -> {
                            32
                        }
                    }
                }
            }
        }
    }
}