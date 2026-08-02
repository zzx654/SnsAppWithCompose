package com.androiddev.data.di

import android.content.Context
import com.androiddev.data.location.LocationProviderImpl
import com.androiddev.data.location.LocationTrackerImpl
import com.androiddev.domain.location.LocationProvider
import com.androiddev.domain.location.LocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationTracker(
        locationTracker: LocationTrackerImpl
    ): LocationTracker

    @Binds
    @Singleton
    abstract fun bindLocationProvider(
        locationProvider: LocationProviderImpl
    ): LocationProvider

    companion object {

        /**@Provides
        @Singleton
        fun provideFusedLocationProviderClient(
            @ApplicationContext context: Context
        ): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(context)
        }**/
    }
}