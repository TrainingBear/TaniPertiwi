package com.trbear9.ui.viewmodel

import com.trbear9.plants.Data
import com.trbear9.plants.E
import com.trbear9.plants.parameters.blob.Plant

class DataModelAcces(plant: Plant) {
    var minTemperature = Data.ecocrop[plant.nama_ilmiah]!!.get(E.O_minimum_temperature);
    var maxTemperature = Data.ecocrop[plant.nama_ilmiah]!!.get(E.O_minimum_temperature);
    var minPH = Data.ecocrop[plant.nama_ilmiah]!!.get(E.O_minimum_ph);
    var maxPH = Data.ecocrop[plant.nama_ilmiah]!!.get(E.O_minimum_ph);
    var minCycle = Data.ecocrop[plant.nama_ilmiah]!!.get(E.MIN_crop_cycle);
    var maxCycle = Data.ecocrop[plant.nama_ilmiah]!!.get(E.MAX_crop_cycle);
    var climateZone = Data.ecocrop[plant.nama_ilmiah]!!.get(E.Climate_zone);
}