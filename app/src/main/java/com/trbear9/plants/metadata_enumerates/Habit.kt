package com.trbear9.plants.metadata_enumerates

import com.trbear9.plants.Constants

enum class Habit(
    override val origin: String,
    override val translation: String
) : TranslatableEnum {
    erect("erect", "Tegak"),
    prostrate_procumbent_semierect("prostrate/procumbent/semi-erect", "Menjalar/Setengah Tegak"),
    climber_scrambler_scadent("climber/scrambler/scadent", "Merambat"),
    acaulescent_or_rosette_plants("acaulescent(or rosette plants)", "Tanpa Batang (Roset)"),
    ;
    override fun toString(): String = translation

    companion object {
        val optimal: String = Constants.Habitat
    }
}