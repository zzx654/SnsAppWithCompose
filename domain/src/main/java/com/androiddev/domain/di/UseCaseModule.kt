package com.androiddev.domain.di

import android.content.Context
import com.androiddev.domain.repository.signup.AuthPhoneRepository
import com.androiddev.domain.repository.createprofile.CreateProfileRepository
import com.androiddev.domain.repository.fcm.FcmRepository
import com.androiddev.domain.repository.signin.SigninRepository
import com.androiddev.domain.repository.signup.SignupRepository
import com.androiddev.domain.repository.tag.TagRepository
import com.androiddev.domain.repository.postdetail.VoteRepository
import com.androiddev.domain.use_case.signup.authphone.AuthPhoneUseCases
import com.androiddev.domain.use_case.signup.authphone.AuthenticateCode
import com.androiddev.domain.use_case.postdetail.CancelVote
import com.androiddev.domain.use_case.createprofile.CheckNickname
import com.androiddev.domain.use_case.createprofile.CreateProfile
import com.androiddev.domain.use_case.createprofile.CreateProfileUseCases
import com.androiddev.domain.use_case.fcm.FcmTokenUseCase
import com.androiddev.domain.use_case.postdetail.GetVoteInfo
import com.androiddev.domain.use_case.tag.GetTags
import com.androiddev.domain.use_case.signup.authphone.RequestPhoneAuthCode
import com.androiddev.domain.use_case.tag.SearchTag
import com.androiddev.domain.use_case.signup.socialsignup.SocialSignUpUseCase
import com.androiddev.domain.use_case.tag.TagUseCases
import com.androiddev.domain.use_case.tag.ToggleFavoriteTag
import com.androiddev.domain.use_case.postdetail.Vote
import com.androiddev.domain.use_case.postdetail.VoteUseCases

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
    fun provideAuthPhoneUseCases(repository: AuthPhoneRepository, @ApplicationContext context: Context): AuthPhoneUseCases {
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
    fun provideCreateProfileUseCases(repository: CreateProfileRepository): CreateProfileUseCases {
        return CreateProfileUseCases(
            createProfile = CreateProfile(repository),
            checkNickname = CheckNickname(repository)
        )
    }
    @Provides
    @Singleton
    fun provideTagUseCases(repository: TagRepository): TagUseCases {
        return TagUseCases(
            getTags = GetTags(repository),
            searchTag = SearchTag(repository),
            toggleFavoriteTag = ToggleFavoriteTag(repository)
        )
    }
    @Provides
    @Singleton
    fun provideVoteUseCases(
        voteRepository: VoteRepository
    ): VoteUseCases {
        return VoteUseCases(
            getVoteInfo = GetVoteInfo(voteRepository),
            vote = Vote(voteRepository),
            cancelVote = CancelVote(voteRepository)
        )
    }
    @Provides
    @Singleton
    fun provideFcmUseCase(
        fcmRepository: FcmRepository
    ): FcmTokenUseCase {
        return FcmTokenUseCase(fcmRepository)
    }
}