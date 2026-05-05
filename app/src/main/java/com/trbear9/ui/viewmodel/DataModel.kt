package com.trbear9.ui.viewmodel

import com.trbear9.plants.Data
import com.trbear9.plants.E
import com.trbear9.plants.getData
import com.trbear9.plants.parameters.blob.Plant

class DataModelAcces(plant: Plant) {
    var minTemperature = getData(plant.nama_ilmiah)?.get(E.O_minimum_temperature);
    var maxTemperature = getData(plant.nama_ilmiah)?.get(E.O_minimum_temperature);
    var minPH = getData(plant.nama_ilmiah)?.get(E.O_minimum_ph);
    var maxPH = getData(plant.nama_ilmiah)?.get(E.O_minimum_ph);
    var minCycle = getData(plant.nama_ilmiah)?.get(E.MIN_crop_cycle);
    var maxCycle = getData(plant.nama_ilmiah)?.get(E.MAX_crop_cycle);
    var climateZone = getData(plant.nama_ilmiah)?.get(E.Climate_zone);
}