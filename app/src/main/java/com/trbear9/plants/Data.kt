package com.trbear9.plants

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.core.StreamWriteConstraints
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.openmeteo.api.Forecast
import com.openmeteo.api.OpenMeteo
import com.openmeteo.api.common.time.Date
import com.openmeteo.api.common.units.TemperatureUnit
import com.trbear9.ui.util.Util
import com.trbear9.ui.util.debug
import com.trbear9.ui.util.info
import com.trbear9.plants.E.Category
import com.trbear9.plants.E.Common_names
import com.trbear9.plants.E.Science_name
import com.trbear9.plants.parameters.GeoParameters
import com.trbear9.plants.parameters.Response
import com.trbear9.plants.parameters.UserVariable
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.util.error
import com.trbear9.ui.util.warn
import com.trbear9.ui.viewmodel.recommendationResult
//import com.trbear9.plants.save
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import lombok.Getter
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.collections.iterator
import kotlin.math.min

fun getData(nama: String): CSVRecord? {
    if (!Data.ecocrop.containsKey(nama)) {
        "Data untuk $nama tidak tersedia".warn("Data Processor")
        return null
//        throw Exception("Data untuk $nama tidak tersedia")
    }
    return Data.ecocrop[nama]!!
}

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@Getter
object Data {
    fun who(): String {
        return """
                            
                                        TIM OPSI SMANEGA 2025
                                        oleh Kukuh & Refan.
                RestAPI has been built by kujatic (trbear) -> https://github.com/TrainingBear (opensource)
                BIG SHOUTOUT TO JASPER                vvvvvvvvvvvvvvvvvvvvvvvvv
                Join komunitas discord kami (Jasper): https://discord.gg/fbAZSd3Hf2
                
                
                """.trimIndent()
    }



    val tags = mutableSetOf<String>()
    var kew = mutableMapOf<String, JsonNode?>()
    val namaUmumToNamaIlmiah = mutableMapOf<String, String>()
    val namaIlmiahToNamaUmum = mutableMapOf<String, String>()

    val plant = mutableMapOf<String, Plant>()
    var plantByTag = mutableMapOf<String, MutableSet<Plant>>()
    val ecocrop = mutableMapOf<String, CSVRecord>()
    fun load(context: Context) {
        CsvHandler.load(context)
        TFService.load(context)
        context.assets.list("serialized")?.forEach {
            if(it!="CSV" && it!="ecocrop.ser") {
                val field = this::class.java.getDeclaredField(it.replace(".ser", ""))
                field.isAccessible = true

                field.set(
                    this, ObjectInputStream(
                        context.assets.open("serialized/$it")
                    ).readObject()
                )
                "Deserialized $it".info("Data Processor")
            }
        }
    }

    fun preLoad() {
        CsvHandler.preload()
//        TFService.load(context)
        val plants = File("plants.json").inputStream().use {
            objectMapper.readValue(it, Array<Plant>::class.java)
        }
        for (item in plants) {
            plant[item.nama_ilmiah] = item
        }
//        Log.d("Data Processor", "Loaded ${plant.size} plants")

        File("KEW.json").inputStream().use {
            val type = object : TypeReference<Map<String, JsonNode>>() {}
            val k = objectMapper.readValue(it, type)
            for (entry in k) {
                val name = entry.key
                val ke = k[name]
                kew[name] = ke
            }
        }
        CsvHandler.ecocropcsv?.forEach {
            if (!ecocrop.containsKey(it[Science_name]))
                ecocrop[it[Science_name]] = it
            loadPlant(it, true)
        }
//        Log.d("Data Processor", "Loaded ${ecocrop.size} ecocrop")
//        Log.d("Data Processor", "Loaded ${kew.size} kew")
//        Log.d("Data Processor", "Loaded ${plant.size} plants")
//        Log.d("Data Processor", "Loaded ${namaUmumToNamaIlmiah.size} namaUmumToNamaIlmiah")
//        Log.d("Data Processor", "Loaded ${namaIlmiahToNamaUmum.size} namaIlmiahToNamaUmum")
//        Log.d("Data Processor", "Loaded ${tags.size} Tags size")

        for (field in Data::class.java.declaredFields) {
            try {
                ObjectOutputStream(save("${field.name}.ser").outputStream())
                    .use{it.writeObject(field.get(this))}
            } catch (_: Throwable){
//                error("Failed to serialize ${field.name}")
                continue
            }
            println("Serialized ${field.name}")
//            "Serialized ${field.name}".info(this::class.simpleName.toString())
        }
    }

    fun loadPlant(record: CSVRecord, load: Boolean = false): Plant {
        val ilmiah = record[Science_name].lowercase()
        if (plant.containsKey(ilmiah) && !load) return plant[ilmiah]!!

        val plant = if (plant[ilmiah] == null) {
//            Log.e("Data Processor", "Full plant version of $ilmiah not found")
            val plant = Plant()
            plant.commonName = ilmiah ?: "Tidak diketahui"
            plant
        } else plant[ilmiah]!!

        plant.nama_ilmiah = ilmiah
        record[Common_names]?.split(", ")?.forEach {
            plant.nama_umum.add(it)
            plantByTag.computeIfAbsent(it) { key ->
                mutableSetOf()
            }.add(plant)
        }
        record[Category]?.split(", ")?.forEach {
            plant.category.add(it)
            tags += it.lowercase()
            plantByTag.computeIfAbsent(it) { key ->
                mutableSetOf()
            }.add(plant)
            plantByTag.computeIfAbsent(Util.translateCategory(it)) { key ->
                mutableSetOf()
            }.add(plant)
            plantByTag.computeIfAbsent("All") { key ->
                mutableSetOf()
            }.add(plant)
        }
        writeTaxonomy(plant)
        tags += ilmiah
        val commonname = plant.commonName?.lowercase() ?: ilmiah
        tags += commonname

        namaUmumToNamaIlmiah[commonname.lowercase()] = ilmiah
        namaIlmiahToNamaUmum[ilmiah] = commonname

        this.plant[ilmiah] = plant
//        Log.d("Data Processor", "Loaded ${plant.commonName} plants")
        return plant
    }

    @Throws(IOException::class)
    fun process(data: UserVariable): Flow<Response> = flow {
        recommendationResult.response = Response(recommendationResult)
        recommendationResult.reset()
        val response = recommendationResult.response
        val prediction = TFService.predict(data.image!!)

        val max = TFService.argmax(prediction)
        val resultSoil = TFService.soils[max.first]
        response.soilMax = max
        response.soilPrediction = TFService.getPredictions(prediction)
        response.predicted = true
        emit(response)

        data.geo.let {
            Log.d("Data Processor", "Getting temperature..")
            try {
                meteo(it)
            } catch (e: Exception){
                "Failed to get temperature".error("Data Processor")
            }
            response.geo = data.geo
            response.parameterLoaded = true
            emit(response)
        }
        data.soil.let {
            it.texture = resultSoil!!.texture
            it.fertility = resultSoil.fertility
            it.drainage = resultSoil.drainage
            it.pH = it.pH ?: resultSoil.pH

            response.soil = it
        }
        Log.d("Data Processor", "DONE")

        val processedData = CsvHandler.process(data)
        for (i in processedData.keys)
            response.target += processedData[i]!!.size
        for (i in processedData.keys) {
            for (ecorecord in processedData[i]!!) {
                val namaIlmiah = ecorecord.get(Science_name)
                response.current = namaIlmiah
                emit(response)
                response.put(i, loadPlant(ecorecord))
                response.progress++
                emit(response)
            }
        }
        response.completed = true
        emit(response)
    }.flowOn(Dispatchers.IO)


    private fun writeTaxonomy(plant: Plant) {
        plant.genus = plant.nama_ilmiah?.split(" ")[0]
        if (kew?.containsKey(plant.nama_ilmiah) == true) {
            plant.full_taxon = kew!![plant.nama_ilmiah]
        } else {
        }
    }

    fun pupuk(category: String) {
    }

    @OptIn(com.openmeteo.api.common.Response.ExperimentalGluedUnitTimeStepValues::class)
    fun meteo(geo: GeoParameters) {
        var max = 0.0f
        var min = 0.0F
        var elevation = 0f
        val meteo = OpenMeteo(geo.latitude.toFloat(), geo.longtitude.toFloat())
        val temperatur = meteo.forecast {
            latitude = geo.latitude.toFloat()
            longitude = geo.longtitude.toFloat()
            temperatureUnit = TemperatureUnit.Celsius
            elevation
            startDate = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 5)
            endDate = Date(System.currentTimeMillis())
            daily = Forecast.Daily {
                listOf(temperature2mMin, temperature2mMax)
            }
        }.getOrThrow()
        Forecast.Daily.run {
            temperatur.daily.getValue(temperature2mMax).run {
                var size = 0
                for (m in values.values){
                    max = Math.max(m?.toFloat() ?: continue, max)
//                    += (m ?: 28.0)
                    "Maximum temperature: $m".debug("METEO")
                    size++
                }
//                max /= size
            }
            temperatur.daily.getValue(temperature2mMin).run {
                var size = 0
                var flag = false
                for (m in values.values) {
                    min = m?.toFloat()?:continue
                    if(!flag){
                        flag = true
                    }
                    min = min(m.toFloat(), min)
//                        (m ?: 20.0)
                    "Minimum temperature: $m".debug("METEO")
                    size++
                }
//                min /= size
            }
        }
        val MPDL = meteo.elevation {
            latitude = geo.latitude.toString()
            longitude = geo.longtitude.toString()
        }.getOrThrow()
        for (f in MPDL.elevation) {
            elevation = f
        }
        elevation /= MPDL.elevation.size
        geo.altitude = elevation.toDouble()
        geo.min = min
        geo.max = max
//        "long: ${geo.longtitude} lat: ${geo.latitude} mdpl: ${geo.altitude}".debug("Data Processor")
//        "temperatur: $min, $max with elevation: $elevation".debug("Data Processor")
    }

    private val objectMapper = ObjectMapper()

    init {
        objectMapper.factory.setStreamReadConstraints(
            StreamReadConstraints.builder()
                .maxStringLength(1_000_000_000)
                .build()
        ).setStreamWriteConstraints(
            StreamWriteConstraints.builder()
                .maxNestingDepth(1_000_000_000)
                .build()
        )
    }
}