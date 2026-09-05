package com.androiddev.snsappwithcompose.di

import com.androiddev.domain.audio.RecordServiceController
import com.androiddev.snsappwithcompose.service.audio.RecordServiceControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindRecordServiceController(
        impl: RecordServiceControllerImpl
    ): RecordServiceController
}