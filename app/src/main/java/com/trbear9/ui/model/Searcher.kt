package com.trbear9.ui.model

import com.trbear9.plants.Data
import com.trbear9.plants.Data.namaIlmiahToNamaUmum
import com.trbear9.plants.Data.namaUmumToNamaIlmiah
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.viewmodel.AbstractPlantResult

object Searcher {
    fun searchByCommonName(
        max: Int = Int.MAX_VALUE,
        query: String,
        output: AbstractPlantResult? = null,
        consumer: (Plant) -> Unit = {},
    ): Set<Plant> {
        val result = mutableSetOf<Plant>()
        var i = 0
        for ((namaUmum, namaIlmah) in namaUmumToNamaIlmiah) {
            val prefix = query.lowercase()
            val target = namaUmum.lowercase()
            val plant = Data.plant[namaIlmah]!!
            if (target.contains(prefix)) {
                result.add(plant)
                consumer(plant)
                output?.add(plant)
                i++
                if (i >= max) break
            }
        }
        return result
    }

    fun searchByScienceName(
        max: Int = Int.MAX_VALUE,
        query: String,
        output: AbstractPlantResult? = null,
        consumer: (Plant) -> Unit = {},
    ): Set<Plant> {
        val result = mutableSetOf<Plant>()
        var i = 0
        for (tag in Data.plant.keys) {
            val prefix = query.lowercase()
            val target = tag.lowercase()
            val plant = Data.plant[tag]!!
            if (target.contains(prefix)) {
                result.add(plant)
                consumer(plant)
                output?.add(plant)
                i++
                if (i >= max) break
            }
        }
        return result
    }
}