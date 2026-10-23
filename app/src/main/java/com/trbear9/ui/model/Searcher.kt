package com.trbear9.ui.model

import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.viewmodel.AbstractPlantResult

object Searcher {
    fun search(
        list: Collection<Plant>,
        q: String
    ): List<Plant> {
        return list
            .filter { p ->
                p.nama_ilmiah.contains(q, ignoreCase = true) ||
                        p.commonName.contains(q, ignoreCase = true) ||
                        p.nama_umum.any { it.contains(q, ignoreCase = true) }
            }
    }
}