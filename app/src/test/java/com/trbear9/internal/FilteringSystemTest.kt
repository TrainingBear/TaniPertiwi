package com.trbear9.internal

import com.trbear9.plants.Constants.Science_name
import com.trbear9.plants.Evaluator
import com.trbear9.plants.parameters.SoilParameters
import com.trbear9.ui.model.PlantRepository
import com.trbear9.ui.model.plantRepository
import com.trbear9.ui.util.info
import com.trbear9.ui.viewmodel.resetParameter
import com.trbear9.ui.viewmodel.soil
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import org.junit.jupiter.api.Test
import java.io.File
import java.io.InputStreamReader

fun streamLocalEcoCrop(init: (CSVRecord) -> Unit) {
    File("EcoCrop_DB.csv").inputStream().use { `is` ->
        InputStreamReader(`is`).use { reader ->
            for (record in CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build()
                .parse(reader)
                .records
            ) {
                init(record)
            }
        }
    }
}

class FilteringSystemTest {
    @Test
    fun check() {
        val plant = PlantRepository.newInstance()
        plant.serialize()
        streamLocalEcoCrop { record -> plant.ecocrop[record[Science_name]] = record }
        "Loaded ${plant.ecocrop.size} ecocrop plants".info(this)
        "Loaded ${plant.plant.size} identified plants".info(this)
        "Loaded ${plant.kew.size} plants taxonomies".info(this)
    }

    @Test
    fun filter() {
        plantRepository = PlantRepository.newInstance()
        plantRepository.serialize()
        streamLocalEcoCrop { record -> plantRepository.ecocrop[record[Science_name]] = record }
        resetParameter()
        soil = SoilParameters.ALLUVIAL
//        var cnt = 0
//        var scanned = 0;
        /*for (plant in plantRepository.getPlants()) {
//            if(cnt == 10) break
            if (!plant.isAvaiable) continue
            scanned++;
            val d = evaluate(plant)
            if (plant.isAvaiable && (d > 0)) {
                "Added ${plant.commonName} as result list".info(this)
                cnt++
            };
        }*/
        Evaluator.evaluateAllEcoCropPlants()
//        "$scanned plants successfully filtered with $cnt plants are accepted!".info(this)
//        assert(cnt >= 10)
    }
}