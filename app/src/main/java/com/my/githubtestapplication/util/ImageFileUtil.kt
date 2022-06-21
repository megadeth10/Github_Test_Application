package com.my.githubtestapplication.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws

/**
 * Created by YourName on 2022/06/13.
 */
object ImageFileUtil {
    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    /**
     * 앱의 외부 이미지 폴더에 파일 전체 삭제
     */
    fun removeFileInPictureFolder(context: Context) {
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        removeDirectory(storageDir)
    }

    fun removeDirectory(file: File?) {
        file?.let {
            if (it.isDirectory) {
                val files = it.listFiles()
                val size = files.size
                for (i in 0 until size) {
                    files[i].delete()
                }
            }
        }
    }

    fun removeFile(file: File?) {
        file?.let {
            if (file.isFile) {
                file.delete()
            }
        }
    }
}