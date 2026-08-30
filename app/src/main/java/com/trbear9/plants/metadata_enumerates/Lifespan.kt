package com.trbear9.plants.metadata_enumerates

import com.trbear9.plants.Constants

enum class Lifespan(
    override val origin: String,
    override val translation: String
) : TranslatableEnum {
    ephemeral("ephemeral", "Efemer"),
    annual("annual", "Semusim (Tahunan)"),
    perennial("perennial", "Menahun"),
    biennial("biennial", "Dua Musim");

    companion object {
        val optimal: String = Constants.Life_span
    }
}