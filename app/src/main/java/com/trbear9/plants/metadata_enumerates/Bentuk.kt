package com.trbear9.plants.metadata_enumerates

import com.trbear9.plants.Constants

enum class Bentuk(
    override val origin: String,
    override val translation: String
) : TranslatableEnum {
    ;

    companion object {
        val optimal: String = Constants.Life_form
    }
}