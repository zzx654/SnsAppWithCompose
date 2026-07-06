package com.androiddev.data.location

import android.annotation.SuppressLint
import com.androiddev.domain.location.LocationProvider
import com.androiddev.domain.location.LocationState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
class LocationProviderImpl @Inject constructor(
    private val locationClient: FusedLocationProviderClient
) : LocationProvider {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationState =
        suspendCoroutine { continuation ->

            locationClient
                .getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                )
                .addOnSuccessListener { location ->

                    continuation.resume(
                        LocationState(
                            latitude = location?.latitude,
                            longitude = location?.longitude
                        )
                    )

                }
                .addOnFailureListener {

                    continuation.resume(
                        LocationState(
                            latitude = null,
                            longitude = null
                        )
                    )

                }

        }
}