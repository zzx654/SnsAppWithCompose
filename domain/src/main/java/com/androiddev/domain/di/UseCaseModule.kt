package com.androiddev.domain.di

import android.content.Context
import com.androiddev.domain.repository.AuthPhoneRepository
import com.androiddev.domain.repository.CreateProfileRepository
import com.androiddev.domain.repository.GetPostsRepository
import com.androiddev.domain.repository.SigninRepository
import com.androiddev.domain.repository.SignupRepository
import com.androiddev.domain.repository.ToggleLikePostRepository
import com.androiddev.domain.repository.UploadPostRepository
import com.androiddev.domain.use_case.AuthPhoneUseCases
import com.androiddev.domain.use_case.AuthenticateCode
import com.androiddev.domain.use_case.CheckNickname
import com.androiddev.domain.use_case.CreateProfile
import com.androiddev.domain.use_case.CreateProfileUseCases
import com.androiddev.domain.use_case.EmailSignIn
import com.androiddev.domain.use_case.EmailSignUp
import com.androiddev.domain.use_case.EmailSignUpUseCases
import com.androiddev.domain.use_case.GetNearPosts
import com.androiddev.domain.use_case.GetPostsUseCases
import com.androiddev.domain.use_case.GetSelectedPost
import com.androiddev.domain.use_case.PostDetailUseCases
import com.androiddev.domain.use_case.RequestEmailAuthCode
import com.androiddev.domain.use_case.RequestPhoneAuthCode
import com.androiddev.domain.use_case.SearchTag
import com.androiddev.domain.use_case.SignInUseCases
import com.androiddev.domain.use_case.SignInWithToken
import com.androiddev.domain.use_case.SocialSignIn
import com.androiddev.domain.use_case.SocialSignUpUseCase
import com.androiddev.domain.use_case.ToggleLikePost
import com.androiddev.domain.use_case.UploadPost
import com.androiddev.domain.use_case.UploadPostUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideSignInUseCases(repository: SigninRepository): SignInUseCases {
        return SignInUseCases(
            socialSignIn = SocialSignIn(repository),
            emailSignIn = EmailSignIn(repository),
            signInWithToken = SignInWithToken(repository)
        )
    }
    @Provides
    @Singleton
    fun provideAuthPhoneUseCases(repository: AuthPhoneRepository,@ApplicationContext context: Context): AuthPhoneUseCases {
        return AuthPhoneUseCases(
            requestAuthCode = RequestPhoneAuthCode(repository,context),
            authenticateCode = AuthenticateCode(repository)
        )
    }
    @Provides
    @Singleton
    fun provideSocialSignUpUseCase(repository: SignupRepository): SocialSignUpUseCase {
        return SocialSignUpUseCase(repository)
    }
    @Provides
    @Singleton
    fun provideEmailSignUpUseCase(repository: SignupRepository,@ApplicationContext context: Context): EmailSignUpUseCases {
        return EmailSignUpUseCases(
            requestAuthCode = RequestEmailAuthCode(repository,context),
            emailSignUp = EmailSignUp(repository)
        )
    }
    @Provides
    @Singleton
    fun provideCreateProfileUseCases(repository: CreateProfileRepository): CreateProfileUseCases {
        return CreateProfileUseCases(
            createProfile = CreateProfile(repository),
            checkNickname = CheckNickname(repository)
        )
    }
    @Provides
    @Singleton
    fun provideUploadPostUseCases(repository: UploadPostRepository): UploadPostUseCases {
        return UploadPostUseCases(
            searchTag = SearchTag(repository),
            uploadPost = UploadPost(repository)
        )
    }
    @Provides
    @Singleton
    fun provideGetPostsUseCases(repository: GetPostsRepository): GetPostsUseCases {
        return GetPostsUseCases(
            getNearPosts = GetNearPosts(repository),
            getSelectedPost = GetSelectedPost(repository)
        )
    }
    @Provides
    @Singleton
    fun providePostDetailUseCases(
        toggleLikePostRepository: ToggleLikePostRepository
    ): PostDetailUseCases {
        return PostDetailUseCases(
            ToggleLikePost = ToggleLikePost(toggleLikePostRepository)
        )
    }
}