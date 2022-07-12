package com.my.githubtestapplication.module

import android.content.Context
import com.my.githubtestapplication.securepreference.SecureSharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Provides
    @Singleton
    fun getSecureSharedPreferences(@ApplicationContext context : Context) =
        SecureSharedPreferences(context)
}