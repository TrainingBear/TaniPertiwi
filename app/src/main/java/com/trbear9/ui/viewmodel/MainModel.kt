package com.trbear9.ui.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.trbear9.plants.parameters.GeoParameters
import com.trbear9.ui.util.DataStore
import com.trbear9.ui.util.Screen
import com.trbear9.ui.util.debug

fun getLocation(context: Context) {
    perm.launch(
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    );
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Tolong izinkan untuk menggunakan fitur ini?", Toast.LENGTH_SHORT).show();
        return
    }
    else gps = true

    val geo = GeoParameters();
    LocationServices.getFusedLocationProviderClient(context)
        .lastLocation
        .addOnSuccessListener {
            if (it != null) {
                geo.latitude = it.latitude;
                geo.longtitude = it.longitude;
            }
        }
    inputs.geo = geo;
    "long: ${geo.longtitude} lat: ${geo.latitude} mdpl: ${geo.altitude}".debug("GETLOCATION")
}

fun ScanTanahButton(context: Context) {
    perm.launch(arrayOf(Manifest.permission.CAMERA))
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        perm.launch(arrayOf(Manifest.permission.CAMERA))
        Toast.makeText(
            context,
            "Tolong izinkan kamera untuk menggunakan fitur ini!",
            Toast.LENGTH_SHORT
        ).show()
    } else {
        cam = true
        DataStore.completeFirstTime()
        nav?.navigate(Screen.camera)
    }
}

