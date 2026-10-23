package com.trbear9.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.trbear9.ui.model.PlantRepository
import com.trbear9.ui.model.findAssetFile
import com.trbear9.ui.model.plantRepository
import org.junit.jupiter.api.Test

object Serialization {
    private val objectMapper = ObjectMapper()

    @Test
    fun serialize() {
        println("user.dir = ${System.getProperty("user.dir")}")
        println("absolute = ${findAssetFile("EcoCrop_DB.csv").absolutePath}")
        println("exists   = ${findAssetFile("EcoCrop_DB.csv").exists()}")
        plantRepository = PlantRepository.newInstance()
        plantRepository.serialize()
    }

    @Test
    fun serializePlantPart() {
    }
}