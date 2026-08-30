package com.trbear9.plants.metadata_enumerates

import com.trbear9.plants.Constants

enum class Drainase(
    override val origin: String,
    override val translation: String
) : TranslatableEnum {
    poorly("poorly (saturated >50% of year)", "Buruk (Tergenang >50% setahun)"),
    well("well (dry spells)", "Baik"),
    excessive("excessive (dry/moderately dry)", "Berlebihan (Sangat Cepat)");

    companion object {
        val absolute: String = Constants.A_soil_drainage
        val optimal: String = Constants.O_soil_drainage
    }
}