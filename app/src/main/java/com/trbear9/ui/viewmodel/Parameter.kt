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
        "   - Main Parameter:".debug(this)
        "       - pH: $pH".debug(this)
        "       - depth: $depth".debug(this)
        "   - Soil Parameter:".debug(this)
        "       - depth: ${soil.depth}".debug(this)
        "       - texture: ${soil.texture}".debug(this)
        "       - fertility: ${soil.fertility}".debug(this)
        "       - drainage: ${soil.drainage}".debug(this)
        "       - pH: ${soil.pH}".debug(this)
        "       - numericDepth: ${soil.numericDepth}".debug(this)
        "   - Geo Parameters:".debug(this)
        "       - iklim: ${geo.iklim}".debug(this)
        "       - latitude: ${geo.latitude}".debug(this)
        "       - longtitude: ${geo.longtitude}".debug(this)
        "       - altitude: ${geo.altitude}".debug(this)
        "       - rainfall: ${geo.rainfall}".debug(this)
        "       - min: ${geo.min}".debug(this)
        "       - max: ${geo.max}".debug(this)
        "   - Custom Parameters:".debug(this)
        "       - category: ${custom.category}".debug(this)
        "       - lifeSpan: ${custom.lifeSpan}".debug(this)
        "       - query: ${custom.query}".debug(this)
        "       - panen: ${custom.panen}".debug(this)
    }
}