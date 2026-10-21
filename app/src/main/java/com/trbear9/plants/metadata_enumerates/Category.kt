package com.trbear9.plants.metadata_enumerates

import androidx.compose.ui.graphics.Color

enum class Category(
    override val origin: String,
    override val translation: String,
    val color: Color = Color(0xFF9E9E9E)
) : TranslatableEnum {
    OTHER("other", "Lainnya",Color.Gray),
    CEREALS_PSEUDOCEREALS("cereals & pseudocereals", "Pseudocereal",Color(0xFF4CAF50)),
    PULLS("pulses (grain legumes)", "Kacang-Kacangan"),
    ROOTS_TUBERS("roots/tubers", "Akar/Umbi", Color.Red),
    FORAGE_PASTURES("forage/pasture", "Pakan ternak", Color.Blue),
    FRUIT_NUT("fruits & nuts", "Buah & kacang", Color(0xFF9C27B0)),
    VEGETABLES("vegetables", "Sayur", Color.Green),
    MATERIALS("materials", "Bahan", Color(0xFFFF9100)),
    ORNAMENTALS_TURF("ornamentals/turf", "Rumput hias", Color(0xFFE91E63)),
    MEDICINALS_AND_ARMOATIC("medicinals & aromatic", "Obat & aromatik", Color(0xFF936123)),
    FOREST_OR_WOOD("forest/wood", "Hutan/Kayu"),
    COVER_CROP("cover crop", "Tanaman Penutup Tanah"),
    ENVIRONMENTAL("environmental", "Lingkungan"),
    WEED("weed", "Gulma");
    override fun toString(): String = translation
}

enum class Climate(
    override val origin: String,
    override val translation: String
) : TranslatableEnum {
    TROPICAL_WET_AND_DRY(
        "tropical wet & dry (Aw)",
        "tropis basah & kering (Aw)"
    ),

    TROPICAL_WET(
        "tropical wet (Ar)",
        "tropis kering"
    ),

    DESERT_OR_ARID(
        "desert or arid (Bw)",
        "gurun"
    ),

    STEPPE_OR_SEMIARID(
        "steppe or semiarid (Bs)",
        "Stepa/Semi-kering"
    ),

    SUBTROPICAL_HUMID(
        "subtropical humid (Cf)",
        "subtropis basah"
    ),

    SUBTROPICAL_DRY_SUMMER(
        "subtropical dry summer (Cs)",
        "mediterania"
    ),

    SUBTROPICAL_DRY_WINTER(
        "subtropical dry winter (Cw)",
        "subtropis lembab"
    ),

    TEMPERATE_OCEANIC(
        "temperate oceanic (Do)",
        "maritim"
    ),

    TEMPERATE_CONTINENTAL(
        "temperate continental (Dc)",
        "Kontinental Sedang"
    ),

    TEMPERATE_WITH_HUMID_WINTERS(
        "temperate with humid winters (Df)",
        "Sedang dengan Musim Dingin Lembab"
    ),

    TEMPERATE_WITH_DRY_WINTERS(
        "temperate with dry winters (Dw)",
        "dingin"
    ),

    BOREAL(
        "boreal (E)",
        "taiga"
    ),

    POLAR(
        "polar (F)",
        "kutub"
    );

    override fun toString(): String = translation

    companion object {
        const val optimal = "Climate_zone"

        fun fromHead(head: String): Climate? =
            entries.find { it.origin == head }
    }
}