package com.trbear9.ui.model

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import java.io.File


class PlantRepositoryTest {

    fun checkResurces(){
        val plants = File("plants.json")
        val ecocrop = File("EcoCrop_DB.csv")
        val kew = File("KEW.json")
        assert(plants.exists())
        assert(ecocrop.exists())
        assert(kew.exists())
        println(plants.path)
        println(ecocrop.path)
        println(kew.path)
    }

    @Test
    fun runAllTest(){
        loadPlantRepository()
        checkPlant()
    }

    @Test
    fun loadPlantRepository() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        loadPlantRepository(context)

        assert(plantRepository.plant.isNotEmpty())
        assert(plantRepository.ecocrop.isNotEmpty())
        assert(plantRepository.kew.isNotEmpty())
    }

    fun checkPlant() {
        val plants = plantRepository.getPlants()
        val plant = plants.first()
        val plant2 = plantRepository.plant.entries.find { it.key == plant.nama_ilmiah }?.value
        assert(plant2 != null)
        assert(plant.nama_ilmiah == plant2!!.nama_ilmiah)
        assert(plant.plantCare == plant2.plantCare)
        assert(plant.description == plant2.description)
        assert(plant.commonName == plant2.commonName)
        println("nama: ${plant.nama_ilmiah} alias ${plant.commonName}")
        println("description: ${plant.description}")
        println("plant care: ${plant.plantCare}")
    }
}