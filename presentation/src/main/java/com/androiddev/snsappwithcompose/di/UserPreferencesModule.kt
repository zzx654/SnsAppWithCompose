package com.androiddev.snsappwithcompose.di

import android.content.Context
import com.androiddev.snsappwithcompose.auth.util.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UserPreferencesModule {
    @Provides
    @ActivityScoped
    fun provideUserPreference(@ActivityContext context: Context) = UserPreferences(context)
}