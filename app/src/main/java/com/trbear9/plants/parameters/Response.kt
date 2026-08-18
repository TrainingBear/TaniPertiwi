package com.trbear9.plants.parameters

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.plants.parameters.blob.SoilCare
import com.trbear9.ui.viewmodel.SoilResult
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.io.Serializable

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
class Response(val delegate: SoilResult) : Serializable {
    var numericDepth by mutableIntStateOf(150)
    var progress by mutableIntStateOf(0)
    var target by mutableIntStateOf(0)
    var predicted by mutableStateOf(false)
    var parameterLoaded by mutableStateOf(false)
    var current by mutableStateOf("Tanaman")
    var completed by mutableStateOf(false)
    var hashCode by mutableStateOf("Tanaman")
    var soilName by mutableStateOf("Tanaman")
    var soilCare by mutableStateOf(SoilCare())
    var soilPrediction: List<Pair<String, Float>> = listOf()
    var soilMax by mutableStateOf(Pair("Belum Diketahui", 0f))
    var total by mutableIntStateOf(0)
    var soil by mutableStateOf(SoilParameters())
    var geo: GeoParameters? = null

    fun put(score: Int, plant: Plant) {
        delegate.add(score, plant)
        total++
    }
}
