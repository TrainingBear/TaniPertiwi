package com.trbear9.plants.metadata_enumerates

import com.trbear9.plants.Constants

enum class Pencahayaan(
    override val origin: String,
    override val translation: String
) : TranslatableEnum {
    heavy_shade("heavy shade", "Naungan Berat"),
    light_shade("light shade", "Naungan Ringan"),
    cloudy_skies("cloudy skies", "Langit Berawan"),
    clear_skies("clear skies", "Langit Cerah"),
    very_bright("very bright", "Sangat Terang");
    override fun toString(): String = translation

    companion object {
        val optimal_min: String = Constants.O_minimum_light_intentsity
        val optimal_max: String = Constants.O_maximum_light_intentsity
        val absolute_min: String = Constants.A_minimum_light_intensity
        val absolute_max: String = Constants.A_maximum_light_intensity
    }
}