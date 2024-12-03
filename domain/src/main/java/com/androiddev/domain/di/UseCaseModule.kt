package com.androiddev.domain.di

import com.androiddev.domain.repository.SigninRepository
import com.androiddev.domain.use_case.SignInUseCases
import com.androiddev.domain.use_case.SocialSignUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideSignInUseCases(repository: SigninRepository): SignInUseCases {
        return SignInUseCases(
            socialSignUseCase = SocialSignUseCase(repository)
        )
    }

}