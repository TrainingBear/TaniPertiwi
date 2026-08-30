package com.trbear9.plants.metadata_enumerates

import com.trbear9.plants.Constants

enum class Fotosintetis(
    override val origin: String,
    override val translation: String
) : TranslatableEnum {
    short_day("short day", "Hari Pendek"),
    long_day("long day", "Hari Panjang"),
    neutral_day("neutral day", "Hari Netral"),
    sensitive("sensitive", "Sensitif");

    companion object {
        val optimal: String = Constants.Photoperiod
    }
}