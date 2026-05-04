package com.trbear9.ui.viewmodel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.trbear9.plants.Data
import com.trbear9.plants.parameters.blob.Plant
import org.jetbrains.annotations.Async

fun SnapshotStateMap<Int, MutableSet<String>>.flatten(order: String): MutableList<Pair<Int, String>> {
    val reversedList = if (order == "Tertinggi") {
        this.toList().asReversed()
    } else this.toList()
    val result = mutableListOf<Pair<Int, String>>()
    reversedList.forEach {
        it.second.forEach { plant ->
            result.add(it.first to plant)
        }
    }
    return result
}

fun completer(query: String, holder: SnapshotStateSet<String>, limit: Int = Int.MAX_VALUE) {
    inputs.soilResult.plants ?: return
    var i = 0
    outer@ for (it in (inputs.soilResult.plants) ?: return) {
        outer2@ for (plant in it.value) {
            val commonName = Data.namaIlmiahToNamaUmum[plant.lowercase()]
            val prefix = query.lowercase()
            if (commonName != null && (commonName.startsWith(prefix) ||
                        commonName.endsWith(prefix) || commonName.contains(prefix))
            ) {
                holder.add(commonName)
                if (i++ > limit) {
                    break@outer
                    break@outer2
                }
            }
        }
    }
}

fun search(query: String, order: String, category: String): MutableList<Pair<Int, String>> {
    val plants = (inputs.soilResult.plantByCategory?.get(category) ?: return mutableListOf())
        .flatten(order)
    val result = mutableListOf<Pair<Int, String>>()
    for (plant in plants) {
        val name = Data.namaIlmiahToNamaUmum[plant.second.lowercase()]
        val prefix = query.lowercase()
        if (name != null && (name.startsWith(prefix) ||
                    name.endsWith(prefix) || name.contains(prefix))
        ) {
            result.add(plant.first to plant.second)
        }
    }
    return result
}