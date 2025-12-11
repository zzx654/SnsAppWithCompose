package com.androiddev.data.di

import android.content.Context
import com.androiddev.data.local.UserPreferences
import com.androiddev.data.remote.api.signup.AuthPhoneApi
import com.androiddev.data.remote.api.postdetail.CommentApi
import com.androiddev.data.remote.api.createprofile.CreateProfileApi
import com.androiddev.data.remote.api.fcm.FcmApi
import com.androiddev.data.remote.api.notification.NotificationApi
import com.androiddev.data.remote.api.postlist.GetPostsApi
import com.androiddev.data.remote.api.postdetail.PostApi
import com.androiddev.data.remote.api.signin.SignInApi
import com.androiddev.data.remote.api.signup.SignUpApi
import com.androiddev.data.remote.api.postdetail.ToggleLikePostApi
import com.androiddev.data.remote.api.tag.TagApi
import com.androiddev.data.remote.api.uploadpost.UploadPostApi
import com.androiddev.data.remote.api.postdetail.VoteApi
import com.androiddev.data.repository.signup.AuthPhoneRepositoryImpl
import com.androiddev.data.repository.postdetail.CommentRepositoryImpl
import com.androiddev.data.repository.createprofile.CreateProfileRepositoryImpl
import com.androiddev.data.repository.fcm.FcmRepositoryImpl
import com.androiddev.data.repository.notification.NotificationRepositoryImpl
import com.androiddev.data.repository.postlist.GetPostsRepositoryImpl
import com.androiddev.data.repository.postdetail.PostRepositoryImpl
import com.androiddev.data.repository.signin.SigninRepositoryImpl
import com.androiddev.data.repository.signup.SignupRepositoryImpl
import com.androiddev.data.repository.postdetail.ToggleLikePostRepositoryImpl
import com.androiddev.data.repository.tag.TagRepositoryImpl
import com.androiddev.data.repository.uploadpost.UploadPostRepositoryImpl
import com.androiddev.data.repository.postdetail.VoteRepositoryImpl
import com.androiddev.domain.repository.signup.AuthPhoneRepository
import com.androiddev.domain.repository.createprofile.CreateProfileRepository
import com.androiddev.domain.repository.fcm.FcmRepository
import com.androiddev.domain.repository.notification.NotificationRepository
import com.androiddev.domain.repository.postdetail.CommentRepository
import com.androiddev.domain.repository.postlist.GetPostsRepository
import com.androiddev.domain.repository.postdetail.PostRepository
import com.androiddev.domain.repository.signin.SigninRepository
import com.androiddev.domain.repository.signup.SignupRepository
import com.androiddev.domain.repository.postdetail.ToggleLikePostRepository
import com.androiddev.domain.repository.tag.TagRepository
import com.androiddev.domain.repository.uploadpost.UploadPostRepository
import com.androiddev.domain.repository.postdetail.VoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideSignInRepository(api: SignInApi, @ApplicationContext context: Context): SigninRepository {
        return SigninRepositoryImpl(api,context)
    }
    @Provides
    @Singleton
    fun provideSignUpRepository(api: SignUpApi, @ApplicationContext context: Context): SignupRepository {
        return SignupRepositoryImpl(api,context)
    }
    @Provides
    @Singleton
    fun provideAuthPhoneRepository(api: AuthPhoneApi, @ApplicationContext context: Context): AuthPhoneRepository {
        return AuthPhoneRepositoryImpl(api,context)
    }
    @Provides
    @Singleton
    fun provideCreateProfileRepository(api: CreateProfileApi, @ApplicationContext context: Context): CreateProfileRepository {
        return CreateProfileRepositoryImpl(api,context)
    }
    @Provides
    @Singleton
    fun provideUploadPostRepository(api: UploadPostApi, @ApplicationContext context: Context): UploadPostRepository {
        return UploadPostRepositoryImpl(api,context)
    }
    @Provides
    @Singleton
    fun provideGetPostsRepository(api: GetPostsApi, @ApplicationContext context: Context): GetPostsRepository {
        return GetPostsRepositoryImpl(api,context)
    }
    @Provides
    @Singleton
    fun provideToggleLikePostRepository(api: ToggleLikePostApi, @ApplicationContext context: Context): ToggleLikePostRepository {
        return ToggleLikePostRepositoryImpl(api,context)
    }

    @Provides
    @Singleton
    fun provideGetCommentsRepository(api: CommentApi, @ApplicationContext context: Context): CommentRepository {
        return CommentRepositoryImpl(api,context)
    }
    @Provides
    @Singleton
    fun provideVoteRepository(api: VoteApi, @ApplicationContext context: Context): VoteRepository {
        return VoteRepositoryImpl(api,context)
    }
    @Provides
    @Singleton
    fun provideTagRepository(api: TagApi, @ApplicationContext context: Context): TagRepository {
        return TagRepositoryImpl(api,context)
    }
    @Provides
    @Singleton
    fun providePostRepository(api: PostApi, @ApplicationContext context: Context): PostRepository {
        return PostRepositoryImpl(
            api = api,
            context = context)
    }
    @Provides
    @Singleton
    fun provideFcmRepository(api: FcmApi, userPreferences: UserPreferences): FcmRepository {
        return FcmRepositoryImpl(
            api = api,
            userPreferences = userPreferences
        )
    }
    @Provides
    @Singleton
    fun provideNotificationRepository(api: NotificationApi, @ApplicationContext context: Context): NotificationRepository {
        return NotificationRepositoryImpl(
            api = api,
            context = context
        )
    }
}