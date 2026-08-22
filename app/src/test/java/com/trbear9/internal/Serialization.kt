package com.trbear9.internal

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.trbear9.plants.CsvHandler
import com.trbear9.ui.model.PlantRepository
import com.trbear9.ui.util.DataSerializer
import com.trbear9.ui.util.debug
import com.trbear9.ui.util.info
import java.io.File
import java.io.ObjectOutputStream
import kotlin.test.Test

class Serialization {
    private val objectMapper = ObjectMapper()

    @Test
    fun serialize() {
        println("user.dir = ${System.getProperty("user.dir")}")
        println("absolute = ${File("EcoCrop_DB.csv").absolutePath}")
        println("exists   = ${File("EcoCrop_DB.csv").exists()}")
        PlantRepository.newInstance().serialize()
    }

    @Test
    fun serializePlantPart() {
    }
}