package com.trbear9.plants.metadata_enumerates

import com.trbear9.plants.Constants

enum class Salinitas(
    override val origin: String,
    override val translation: String
) : TranslatableEnum {
    none("none", "Tidak Ada"),
    low("low", "Rendah"),
    moderate("moderate", "Sedang"),
    high("high", "Tinggi");
    override fun toString(): String = translation

    companion object {
        val absolute: String = Constants.A_soil_salinity
        val optimal: String = Constants.O_soil_salinity
    }
}