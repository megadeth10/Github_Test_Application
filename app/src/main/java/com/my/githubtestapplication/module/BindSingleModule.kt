package com.my.githubtestapplication.module

import com.my.githubtestapplication.network.api.PostService
import com.my.githubtestapplication.network.response.Post
import com.my.githubtestapplication.util.Log
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.internal.wait
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by YourName on 2022/07/14.
 */

interface LogRepository {
    suspend fun add(msg: String)
    suspend fun getLogs() :List<String>
}

@Singleton
class LogDBRepository @Inject constructor(
    private val postService : PostService
): LogRepository {
    init {
        Log.e(LogDBRepository::class.simpleName, "init() postService: $postService")
    }
    override suspend fun add(msg : String) {
    }

    override suspend fun getLogs() : List<String> {
        return listOf("aaaa", "bbb")
    }

    fun implFunc() {

    }
}

@InstallIn(SingletonComponent::class)
@Module
abstract class LogDBModule {
    @Binds
    @Singleton
    abstract fun bindLogDBRepository(impl:LogDBRepository): LogRepository
}