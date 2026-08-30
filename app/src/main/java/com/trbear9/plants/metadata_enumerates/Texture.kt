package com.trbear9.plants.metadata_enumerates

import com.trbear9.plants.Constants

enum class Texture(
    override val origin: String,
    override val translation: String
) : TranslatableEnum {
    heavy("heavy", "Berat (Liat)"),
    medium("medium", "Sedang (Lempung)"),
    light("light", "Ringan (Pasir)"),
    wide("wide", "Luas"),
    organic("organic", "Organik");

    companion object {
        val absolute: String = Constants.A_soil_texture
        val optimal: String = Constants.O_soil_texture
    }
}