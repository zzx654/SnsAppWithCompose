package com.androiddev.snsappwithcompose.common.util

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient

@SuppressLint("MissingPermission")
fun fetchLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onAddressFetched: (Double?,Double?) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            onAddressFetched(it.latitude,it.longitude)
        } ?: onAddressFetched(null,null)
    }.addOnFailureListener {
        onAddressFetched(null,null)
    }
}