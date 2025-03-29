package com.androiddev.data.di

import android.content.Context
import com.androiddev.data.remote.api.AuthPhoneApi
import com.androiddev.data.remote.api.CreateProfileApi
import com.androiddev.data.remote.api.GetPostsApi
import com.androiddev.data.remote.api.SignInApi
import com.androiddev.data.remote.api.SignUpApi
import com.androiddev.data.remote.api.UploadPostApi
import com.androiddev.data.repository.AuthPhoneRepositoryImpl
import com.androiddev.data.repository.CreateProfileRepositoryImpl
import com.androiddev.data.repository.GetPostsRepositoryImpl
import com.androiddev.data.repository.SigninRepositoryImpl
import com.androiddev.data.repository.SignupRepositoryImpl
import com.androiddev.data.repository.UploadPostRepositoryImpl
import com.androiddev.domain.repository.AuthPhoneRepository
import com.androiddev.domain.repository.CreateProfileRepository
import com.androiddev.domain.repository.GetPostsRepository
import com.androiddev.domain.repository.SigninRepository
import com.androiddev.domain.repository.SignupRepository
import com.androiddev.domain.repository.UploadPostRepository
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
    fun provideSignInRepository(api: SignInApi,@ApplicationContext context: Context): SigninRepository {
        return SigninRepositoryImpl(api,context)
    }
    @Provides
    @Singleton
    fun provideSignUpRepository(api: SignUpApi,@ApplicationContext context: Context): SignupRepository {
        return SignupRepositoryImpl(api,context)
    }
    @Provides
    @Singleton
    fun provideAuthPhoneRepository(api: AuthPhoneApi,@ApplicationContext context: Context): AuthPhoneRepository {
        return AuthPhoneRepositoryImpl(api,context)
    }
    @Provides
    @Singleton
    fun provideCreateProfileRepository(api: CreateProfileApi,@ApplicationContext context: Context): CreateProfileRepository {
        return CreateProfileRepositoryImpl(api,context)
    }
    @Provides
    @Singleton
    fun provideUploadPostRepository(api: UploadPostApi,@ApplicationContext context: Context): UploadPostRepository {
        return UploadPostRepositoryImpl(api,context)
    }
    @Provides
    @Singleton
    fun provideGetPostsRepository(api: GetPostsApi,@ApplicationContext context: Context): GetPostsRepository {
        return GetPostsRepositoryImpl(api,context)
    }
}