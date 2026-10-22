package com.trbear9.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.trbear9.plants.parameters.Response
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.util.Util
import com.trbear9.ui.util.info
import kotlinx.coroutines.flow.Flow

class SoilResult {
    var plants: SnapshotStateMap<Int, MutableSet<Plant>> = mutableStateMapOf()
    var plantByCategory: SnapshotStateMap<String, SnapshotStateMap<Int, MutableSet<Plant>>> =
        mutableStateMapOf()
    var loaded by mutableStateOf(false)
    var flow: Flow<Response>? = null
    var response by mutableStateOf(Response(this))
    var collected by mutableStateOf(false)
    var collecting by mutableStateOf(false)

    fun add(score: Int, plant: Plant) {
//        "Loaded ${plant.nama_ilmiah} with score $score".info("Data Processor")
        plants.computeIfAbsent(score) { mutableSetOf() }.add(plant)
        for (category in plant.category) {
            plantByCategory.computeIfAbsent(
                Util.translateCategory(category)
            ) { mutableStateMapOf() }
                .computeIfAbsent(score) { mutableSetOf() }
                .add(plant)
        }
        plantByCategory.computeIfAbsent("All") { mutableStateMapOf() }
            .computeIfAbsent(score) { mutableSetOf() }
            .add(plant)
    }

    fun reset() {
        plantByCategory.clear()
        plants.clear()
    }
}

class SearchResult : AbstractPlantResult() {
    override fun add(plant: Plant) {
        plants.add(plant)
        for (category in plant.getEnumCategory()) {
            plantByCategory.computeIfAbsent(category.toString()) {
                mutableSetOf()
            }.add(plant)
        }
        plantByCategory.computeIfAbsent("All") {
            mutableSetOf()
        }.add(plant)
    }
}

abstract class AbstractPlantResult() : PlantResult {
    var plants: MutableSet<Plant> = mutableSetOf()
    var plantByCategory: SnapshotStateMap<String, MutableSet<Plant>> = mutableStateMapOf()
    fun reset() {
        plants.clear()
        plantByCategory.clear()
    }
}

interface PlantResult {
    fun add(plant: Plant)
}