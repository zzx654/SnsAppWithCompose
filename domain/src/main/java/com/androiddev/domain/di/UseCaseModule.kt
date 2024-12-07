package com.androiddev.domain.di

import com.androiddev.domain.repository.AuthPhoneRepository
import com.androiddev.domain.repository.SigninRepository
import com.androiddev.domain.repository.SignupRepository
import com.androiddev.domain.use_case.AuthPhoneUseCases
import com.androiddev.domain.use_case.AuthenticateCode
import com.androiddev.domain.use_case.RequestPhoneAuthCode
import com.androiddev.domain.use_case.SignInUseCases
import com.androiddev.domain.use_case.SocialSignIn
import com.androiddev.domain.use_case.SocialSignUpUseCase
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
            socialSignIn = SocialSignIn(repository)
        )
    }
    @Provides
    @Singleton
    fun provideAuthPhoneUseCases(repository: AuthPhoneRepository): AuthPhoneUseCases {
        return AuthPhoneUseCases(
            requestAuthCode = RequestPhoneAuthCode(repository),
            authenticateCode = AuthenticateCode(repository)
        )
    }
    @Provides
    @Singleton
    fun provideSocialSignUpUseCase(repository: SignupRepository): SocialSignUpUseCase {
        return SocialSignUpUseCase(repository)
    }

}