package com.trbear9.plants.metadata_enumerates

import com.trbear9.plants.Constants

enum class Kesuburan(
    override val origin: String,
    override val translation: String
) : TranslatableEnum {
    low("low", "Rendah"),
    moderate("moderate", "Sedang"),
    high("high", "Tinggi");

    companion object {
        val absolute: String = Constants.A_soil_fertility
        val optimal: String = Constants.O_soil_fertility
    }
}