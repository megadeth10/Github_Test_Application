package com.my.githubtestapplication.module

import android.content.Context
import com.my.githubtestapplication.network.Network
import com.my.githubtestapplication.network.api.PostService
import com.my.githubtestapplication.securepreference.SecureSharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    @Singleton
    fun getSecureSharedPreferences(@ApplicationContext context : Context) =
        SecureSharedPreferences(context)

    @Provides
    @Singleton
    fun getNetwork() = Network()

    @Provides
    @Singleton
    fun getPostService(network : Network) = PostService(network)
}