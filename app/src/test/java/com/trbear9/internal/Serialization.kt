package com.trbear9.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.trbear9.ui.model.PlantRepository
import org.junit.jupiter.api.Test
import java.io.File

object Serialization {
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