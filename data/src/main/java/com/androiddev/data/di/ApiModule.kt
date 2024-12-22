package com.androiddev.data.di

import com.androiddev.data.BuildConfig
import com.androiddev.data.remote.api.AuthPhoneApi
import com.androiddev.data.remote.api.SignInApi
import com.androiddev.data.remote.api.SignUpApi
import com.androiddev.data.util.UserPreferences
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl:String,userPreferences: UserPreferences): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain->
                        val authToken:String?
                        runBlocking { authToken = userPreferences.authToken.first() }
                        chain.proceed(chain.request().newBuilder().also{
                            it.addHeader("Authorization","Bearer $authToken")
                        }.build())
                    }
                    .also{client->
                        if(BuildConfig.DEBUG){
                            val logging= HttpLoggingInterceptor()
                            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                            client.addInterceptor(logging)
                        }
                    }
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }
    @Provides
    @Singleton
    fun provideAuthPhoneApi(retrofit: Retrofit): AuthPhoneApi = retrofit.create(AuthPhoneApi::class.java)

    @Provides
    @Singleton
    fun provideSignInApi(retrofit: Retrofit): SignInApi = retrofit.create(SignInApi::class.java)
    @Provides
    @Singleton
    fun provideSignUpApi(retrofit: Retrofit): SignUpApi = retrofit.create(SignUpApi::class.java)

}