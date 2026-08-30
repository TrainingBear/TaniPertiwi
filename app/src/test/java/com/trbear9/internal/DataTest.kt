package com.trbear9.internal

import com.trbear9.plants.Constants
import com.trbear9.ui.model.PlantRepository
import com.trbear9.ui.model.plantRepository
import org.junit.jupiter.api.Test

class DataTest {

    @Test
    fun checkPlants(){
        plantRepository = PlantRepository.newInstance()
        plantRepository.serialize()
        streamLocalEcoCrop { record ->
            plantRepository.ecocrop[record[Constants.Science_name]] = record
        }

        for (plant in plantRepository.plant.values) {

        }
    }
}