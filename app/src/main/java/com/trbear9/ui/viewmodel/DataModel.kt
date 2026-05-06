package com.trbear9.ui.viewmodel

import com.trbear9.plants.Data
import com.trbear9.plants.E
import com.trbear9.plants.getData
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.util.Util

class DataModelAcces(plant: Plant) {
    val namaIlmiah = plant.nama_ilmiah;
    var namaUmum = plant.commonName;
    var deskripsi = plant.description;
    var minTemperature = getData(plant.nama_ilmiah)?.get(E.O_minimum_temperature);
    var maxTemperature = getData(plant.nama_ilmiah)?.get(E.O_minimum_temperature);
    var minPH = getData(plant.nama_ilmiah)?.get(E.O_minimum_ph);
    var maxPH = getData(plant.nama_ilmiah)?.get(E.O_minimum_ph);
    var minCycle = getData(plant.nama_ilmiah)?.get(E.MIN_crop_cycle);
    var maxCycle = getData(plant.nama_ilmiah)?.get(E.MAX_crop_cycle);
    var salinity = getData(plant.nama_ilmiah)?.get(E.A_soil_salinity);
    var photoperiod = getData(plant.nama_ilmiah)?.get(E.Photoperiod);
    var resiko = getData(plant.nama_ilmiah)?.get(E.Introduction_risk);
    var kategori = Util.bulkCategoryTranslate(getData(namaIlmiah)?.get(E.Category))
    var iklim = Util.bulkClimateTranslate(getData(namaIlmiah)?.get(E.Climate_zone))
    var skalaProduksi = getData(plant.nama_ilmiah)?.get(E.Production_system)
    var author = getData(plant.nama_ilmiah)?.get(E.Authority)
    var sinonim = getData(namaIlmiah)?.get(E.Synonyms)
    var takson = plant.taxon
}