package com.trbear9.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.Composable
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.util.getContext

val url = "https://r2-worker.jasperproject.workers.dev"
//600x400 image size
@Composable
fun getPlantImage(plant: Plant): ImageRequest {
    val encodedName = Uri.encode("${plant.nama_ilmiah}.webp")
    return ImageRequest.Builder(getContext())
        .data("$url/$encodedName")
        .crossfade(true)
        .size(600, 400)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()
}

@Composable
fun getDetailedImage(plant: Plant?) : ImageRequest {
    val encodedName = Uri.encode("${plant?.nama_ilmiah}.webp")
    return ImageRequest.Builder(getContext())
        .data("$url/$encodedName")
        .crossfade(true)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()
}