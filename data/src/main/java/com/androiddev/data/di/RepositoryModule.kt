package com.androiddev.data.di

import com.androiddev.data.remote.api.SignInApi
import com.androiddev.data.repository.SigninRepositoryImpl
import com.androiddev.domain.repository.SigninRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideSignInRepository(api: SignInApi): SigninRepository {
        return SigninRepositoryImpl(api)
    }
}