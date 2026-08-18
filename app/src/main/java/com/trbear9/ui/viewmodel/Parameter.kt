package com.trbear9.ui.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.trbear9.plants.parameters.GeoParameters
import com.trbear9.plants.parameters.Response
import com.trbear9.plants.parameters.SoilParameters
import kotlinx.coroutines.flow.Flow

var pH by mutableFloatStateOf(-1f)
var depth by mutableFloatStateOf(20f)
var response: Flow<Response>? = null
var loaded by mutableStateOf(false)
var geo by mutableStateOf(GeoParameters())
var soil by mutableStateOf(SoilParameters())
var image: Bitmap? = null
var recommendationResult = SoilResult()
var databaseSearchResult = SearchResult()

class Parameter : ViewModel() {
}