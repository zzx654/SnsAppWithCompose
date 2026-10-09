package com.androiddev.data.di

import com.androiddev.data.repository.signup.AuthPhoneRepositoryImpl
import com.androiddev.data.repository.postdetail.CommentRepositoryImpl
import com.androiddev.data.repository.createprofile.CreateProfileRepositoryImpl
import com.androiddev.data.repository.fcm.FcmRepositoryImpl
import com.androiddev.data.repository.notification.NotificationRepositoryImpl
import com.androiddev.data.repository.postdetail.PostRepositoryImpl
import com.androiddev.data.repository.signin.SigninRepositoryImpl
import com.androiddev.data.repository.signup.SignupRepositoryImpl
import com.androiddev.data.repository.postdetail.ToggleLikePostRepositoryImpl
import com.androiddev.data.repository.tag.TagRepositoryImpl
import com.androiddev.data.repository.uploadpost.UploadPostRepositoryImpl
import com.androiddev.data.repository.postdetail.VoteRepositoryImpl
import com.androiddev.data.repository.postlist.PostListRepositoryImpl
import com.androiddev.data.repository.user.UserRepositoryImpl
import com.androiddev.domain.repository.signup.AuthPhoneRepository
import com.androiddev.domain.repository.createprofile.CreateProfileRepository
import com.androiddev.domain.repository.fcm.FcmRepository
import com.androiddev.domain.repository.notification.NotificationRepository
import com.androiddev.domain.repository.postdetail.CommentRepository
import com.androiddev.domain.repository.postdetail.PostRepository
import com.androiddev.domain.repository.signin.SigninRepository
import com.androiddev.domain.repository.signup.SignupRepository
import com.androiddev.domain.repository.postdetail.ToggleLikePostRepository
import com.androiddev.domain.repository.tag.TagRepository
import com.androiddev.domain.repository.uploadpost.UploadPostRepository
import com.androiddev.domain.repository.postdetail.VoteRepository
import com.androiddev.domain.repository.postlist.PostListRepository
import com.androiddev.domain.repository.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUploadPostRepository(
        uploadPostRepositoryImpl: UploadPostRepositoryImpl
    ): UploadPostRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
    @Binds
    @Singleton
    abstract fun bindPostListRepository(
        postListRepositoryImpl: PostListRepositoryImpl
    ): PostListRepository

    @Binds
    @Singleton
    abstract fun bindSignInRepository(
        signinRepositoryImpl: SigninRepositoryImpl
    ): SigninRepository

    @Binds
    @Singleton
    abstract fun bindSignUpRepository(
        signupRepositoryImpl: SignupRepositoryImpl
    ): SignupRepository

    @Binds
    @Singleton
    abstract fun bindAuthPhoneRepository(
        authPhoneRepositoryImpl: AuthPhoneRepositoryImpl
    ): AuthPhoneRepository

    @Binds
    @Singleton
    abstract fun bindCreateProfileRepository(
        createProfileRepositoryImpl: CreateProfileRepositoryImpl
    ): CreateProfileRepository

    @Binds
    @Singleton
    abstract fun bindToggleLikePostRepository(
        toggleLikePostRepositoryImpl: ToggleLikePostRepositoryImpl
    ): ToggleLikePostRepository

    @Binds
    @Singleton
    abstract fun bindGetCommentsRepository(
        getCommentsPostRepositoryImpl: CommentRepositoryImpl
    ): CommentRepository

    @Binds
    @Singleton
    abstract fun bindVoteRepository(
        voteRepositoryImpl: VoteRepositoryImpl
    ): VoteRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(
        tagRepositoryImpl: TagRepositoryImpl
    ): TagRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(
        postRepositoryImpl: PostRepositoryImpl
    ): PostRepository

    @Binds
    @Singleton
    abstract fun bindFcmRepository(
        fcmRepositoryImpl: FcmRepositoryImpl
    ): FcmRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

}

