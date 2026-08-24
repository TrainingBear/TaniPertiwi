package com.trbear9.internal

import com.trbear9.plants.Evaluator
import com.trbear9.plants.parameters.SoilParameters
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.model.PlantRepository
import com.trbear9.ui.model.plantRepository
import com.trbear9.ui.util.info
import com.trbear9.ui.viewmodel.resetParameter
import com.trbear9.ui.viewmodel.soil
import org.junit.jupiter.api.Test

object FilteringSystemTest {
    @Test
    fun filter(){
        plantRepository = PlantRepository.newInstance()
        plantRepository.serialize()
        resetParameter()
        soil = SoilParameters.ALLUVIAL
        var cnt = 0
        for (plant in plantRepository.getPlants()) {
            if(cnt == 10) break;
            if(!plant.isAvaiable) continue
            val d = evaluate(plant)
            if(plant.isAvaiable && d > 0) {
                "Added ${plant.commonName} as result list".info(this)
                cnt++
            };
        }
    }

    fun evaluate(plant: Plant): Int{
        assert(plant.record != null)
        val result = Evaluator.evaluateAll(plant.record!!)
        return result
    }
}