package com.androiddev.data.di

import com.androiddev.data.audio.AndroidAudioPlayer
import com.androiddev.data.audio.AndroidAudioRecorder
import com.androiddev.data.audio.AudioFileManagerImpl
import com.androiddev.domain.audio.AudioFileManager
import com.androiddev.domain.audio.AudioPlayer
import com.androiddev.domain.audio.AudioRecorder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AudioModule {

    /**@Provides
    @Singleton
    fun provideAudioRecorder(
        @ApplicationContext context: Context
    ): AudioRecorder = AndroidAudioRecorder(context)

    @Provides
    @Singleton
    fun provideAudioPlayer(
        @ApplicationContext context: Context
    ): AudioPlayer = AndroidAudioPlayer(context)**/

    @Binds
    @Singleton
    abstract fun bindAudioRecorder(
        impl: AndroidAudioRecorder
    ): AudioRecorder

    @Binds
    @Singleton
    abstract fun bindAudioPlayer(
        impl: AndroidAudioPlayer
    ): AudioPlayer

    @Binds
    @Singleton
    abstract fun bindAudioFileManager(
        impl: AudioFileManagerImpl
    ): AudioFileManager
}