package com.androiddev.data.di

import android.content.Context
import com.androiddev.data.location.LocationProviderImpl
import com.androiddev.domain.location.LocationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    /**@Provides
    @Singleton
    fun provideLocationClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {

        return LocationServices.getFusedLocationProviderClient(context)

    }**/

    @Provides
    @Singleton
    fun provideLocationProvider(
        locationClient: FusedLocationProviderClient
    ): LocationProvider {

        return LocationProviderImpl(locationClient)

    }
}