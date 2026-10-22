package com.trbear9.ui.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.trbear9.plants.parameters.CustomParameters
import com.trbear9.plants.parameters.GeoParameters
import com.trbear9.plants.parameters.Response
import com.trbear9.plants.parameters.SoilParameters
import com.trbear9.ui.util.debug
import com.trbear9.ui.util.info
import kotlinx.coroutines.flow.Flow

var pH by mutableStateOf<Float?>(null)
var depth by mutableStateOf<Float?>(null)
var image: Bitmap? = null
var response: Flow<Response>? = null
var loaded by mutableStateOf(false)
var geo by mutableStateOf(GeoParameters())
var soil by mutableStateOf(SoilParameters())
var custom by mutableStateOf(CustomParameters())

// output
var recommendationResult = SoilResult()
var databaseSearchResult = SearchResult()

fun resetParameter(){
    pH = null
    depth = null
    response = null
    loaded = false
    geo = GeoParameters()
    soil = SoilParameters()
    custom = CustomParameters()
    recommendationResult.reset()
    databaseSearchResult.reset()
}

object Parameter : ViewModel() {
    fun start2info2Process() {
        "Starting to process user variable:".info(this)
        "   - Main Parameter:".info(this)
        "       - pH: $pH".info(this)
        "       - depth: $depth".info(this)
        "   - Soil Parameter:".info(this)
        "       - depth: ${soil.depth}".info(this)
        "       - texture: ${soil.texture}".info(this)
        "       - fertility: ${soil.fertility}".info(this)
        "       - drainage: ${soil.drainage}".info(this)
        "       - pH: ${soil.pH}".info(this)
        "       - numericDepth: ${soil.numericDepth}".info(this)
        "   - Geo Parameters:".info(this)
        "       - iklim: ${geo.iklim}".info(this)
        "       - latitude: ${geo.latitude}".info(this)
        "       - longtitude: ${geo.longtitude}".info(this)
        "       - altitude: ${geo.altitude}".info(this)
        "       - rainfall: ${geo.rainfall}".info(this)
        "       - min: ${geo.min}".info(this)
        "       - max: ${geo.max}".info(this)
        "   - Custom Parameters:".info(this)
        "       - category: ${custom.category}".info(this)
        "       - lifeSpan: ${custom.lifeSpan}".info(this)
        "       - query: ${custom.query}".info(this)
        "       - panen: ${custom.panen}".info(this)
    }
}