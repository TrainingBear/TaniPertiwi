package com.trbear9.ui.model

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.trbear9.plants.E
import com.trbear9.plants.E.Science_name
import com.trbear9.plants.TFService
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.util.debug
import com.trbear9.ui.util.info
import com.trbear9.ui.util.save
import java.io.Serializable
import java.util.TreeMap
import android.content.Context
import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.core.StreamWriteConstraints
import com.fasterxml.jackson.databind.ObjectMapper
import com.openmeteo.api.Forecast
import com.openmeteo.api.OpenMeteo
import com.openmeteo.api.common.units.TemperatureUnit
import com.trbear9.plants.E.Category
import com.trbear9.plants.E.Common_names
import com.trbear9.plants.Evaluator.evaluateAll
import com.trbear9.plants.parameters.GeoParameters
import com.trbear9.ui.util.error
import com.trbear9.plants.parameters.Response
import com.trbear9.ui.util.DataSerializer
import com.trbear9.ui.util.Util
import com.trbear9.ui.util.warn
import com.trbear9.ui.viewmodel.Parameter
import com.trbear9.ui.viewmodel.geo
import com.trbear9.ui.viewmodel.image
import com.trbear9.ui.viewmodel.recommendationResult
import com.trbear9.ui.viewmodel.soil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.math.min

private val objectMapper = ObjectMapper().also {
    it.factory.setStreamReadConstraints(
        StreamReadConstraints.builder()
            .maxStringLength(1_000_000_000)
            .build()
    ).setStreamWriteConstraints(
        StreamWriteConstraints.builder()
            .maxNestingDepth(1_000_000_000)
            .build()
    )
}
const val PLANTS = "plants.ser"
const val KEW = "kew.ser"
const val TAG = "tags.ser"
const val Umum2Ilmiah = "umum2ilmiah.ser"
const val Ilmiah2Umum = "ilmiah2umum.ser"
const val ECOCROP = "ecocrop.ser"
const val REPOSITORY = "plantrepository.ser"

lateinit var plantRepository: PlantRepository
fun loadPlantRepository(context: Context) {
    TFService.load(context)
    plantRepository = DataSerializer.read(context.assets.open(REPOSITORY))
    context.assets.open("EcoCrop_DB.csv").use { `is` ->
        InputStreamReader(`is`).use { reader ->
            for (record in CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build()
                .parse(reader)
                .records
            ) {
                plantRepository.ecocrop[record[Science_name]] = record
            }
        }
    }
}

class PlantRepository private constructor() : Serializable {
    val tags = HashSet<String>()
    var kew = HashMap<String, JsonNode?>()
    val namaUmumToNamaIlmiah = HashMap<String, String>()
    val namaIlmiahToNamaUmum = HashMap<String, String>()

    val plant = HashMap<String, Plant>()
    var plantByTag = HashMap<String, MutableSet<Plant>>()
    val ecocrop = HashMap<String, CSVRecord>()


    fun serialize() {
        try {
            File("EcoCrop_DB.csv").inputStream().use { `is` ->
                InputStreamReader(`is`).use { reader ->
                    for (record in CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(reader)
                        .records
                    ) {
                        ecocrop[record[Science_name]] = record
                    }
                }
            }

            //OBSOLETE
//            File("Perawatan.csv").inputStream().use { `is` ->
//                InputStreamReader(`is`).use { reader ->
//                    perawatan = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).records
//                }
//            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }

        val plants = File("plants.json").inputStream().use {
            objectMapper.readValue(it, Array<Plant>::class.java)
        }
        for (item in plants) plant[item.nama_ilmiah] = item.also {
            val record = ecocrop[it.nama_ilmiah] ?: run {
                "Cant find any related data about ${it.nama_ilmiah}".error(this)
                return@also
            }
            it.load(record)
            it.record = record
            it.isAvaiable = true
            "Loaded ${it.nama_ilmiah}".info(this)
        }
        File("KEW.json").inputStream().use {
            val type = object : TypeReference<Map<String, JsonNode>>() {}
            val k = objectMapper.readValue(it, type)
            for (entry in k) {
                val name = entry.key
                val ke = k[name]
                kew[name] = ke
            }
        }
        "Loaded ${ecocrop.size} ecocrop".debug(this)
        "Loaded ${kew.size} kew".debug(this)
        "Loaded ${plant.size} plants".debug(this)
        "Loaded ${namaUmumToNamaIlmiah.size} namaUmumToNamaIlmiah".debug(this)
        "Loaded ${namaIlmiahToNamaUmum.size} namaIlmiahToNamaUmum".debug(this)
        "Loaded ${tags.size} Tags size".debug(this)

        ecocrop.clear()
        DataSerializer.write(save("src/main/assets/$REPOSITORY"), this)
    }

    fun isAuthored(record: CSVRecord): Boolean {
        return record.get(E.Authority) != null || !record.get(E.Authority).isEmpty()
    }

    fun loadPlant(record: CSVRecord, load: Boolean = false): Plant {
        val ilmiah = record[Science_name].lowercase()
        if (plant.containsKey(ilmiah) && !load) return plant[ilmiah]!!

        val plant = if (plant[ilmiah] == null) {
            "Full plant version of $ilmiah not found".warn(this)
            val plant = Plant(record)
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
//        Log.d(this, "Loaded ${plant.commonName} plants")
        plant.load(record)
        plant.record = record
        return plant
    }

    @Throws(IOException::class)
    fun process(): Flow<Response> = flow {
        Parameter.start2info2Process()
        recommendationResult.response = Response(recommendationResult)
        recommendationResult.reset()
        recommendationResult.collecting = true
        val response = recommendationResult.response
        val prediction = TFService.predict(image!!)

        val max = TFService.argmax(prediction)
        val resultSoil = TFService.soils[max.first]
        response.soilMax = max
        response.soilPrediction = TFService.getPredictions(prediction)
        response.predicted = true
        emit(response)
        "Soil has been predicted!".info(this)

        try {
            "Getting temperature".info(this)
            meteo(geo)
        } catch (_: Exception) { "Failed to get temperature".error(this) }
        response.geoParameterLoaded = true
        emit(response)
        "Successfully loaded geo parameter!".info(this)
        soil.let {
            it.texture = resultSoil!!.texture
            it.fertility = resultSoil.fertility
            it.drainage = resultSoil.drainage
            it.pH = it.pH ?: resultSoil.pH
        }
        val processedData = TreeMap<Int, MutableSet<CSVRecord>>()
        if (ecocrop.isEmpty()) throw Exception("EcoCrop_DB.csv not found, coba untuk load()")

        for ((name, record) in ecocrop) {
            val score = evaluateAll(record)
            if (score < 0) "evaluated '${record.get(E.Science_name)}' with score: $score".info(this)

            if (isAuthored(record) && score > 0)
                processedData.computeIfAbsent(score) { k: Int? -> HashSet<CSVRecord>() }
                    .add(record)
        }
        for (i in processedData.keys)
            response.target += processedData[i]!!.size
        for (i in processedData.keys) {
            for (ecorecord in processedData[i]!!) {
                val namaIlmiah = ecorecord.get(Science_name)
                response.current = namaIlmiah
                emit(response)
                "Loaded $namaIlmiah with score: $i".info(this)
                response.put(i, loadPlant(ecorecord))
                response.progress++
                emit(response)
            }
        }
        response.completed = true
        recommendationResult.collecting = false
        emit(response)
    }.flowOn(Dispatchers.Default)


    private fun writeTaxonomy(plant: Plant) {
        plant.genus = plant.nama_ilmiah.split(" ")[0]
        if (kew.containsKey(plant.nama_ilmiah)) {
            plant.full_taxon = kew[plant.nama_ilmiah]
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
            startDate =
                com.openmeteo.api.common.time.Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 5)
            endDate = com.openmeteo.api.common.time.Date(System.currentTimeMillis())
            daily = Forecast.Daily {
                listOf(temperature2mMin, temperature2mMax)
            }
        }.getOrThrow()
        Forecast.Daily.run {
            temperatur.daily.getValue(temperature2mMax).run {
                var size = 0
                for (m in values.values) {
                    max = (m?.toFloat() ?: continue).coerceAtLeast(max)
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
                    min = m?.toFloat() ?: continue
                    if (!flag) {
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
//        "long: ${geo.longtitude} lat: ${geo.latitude} mdpl: ${geo.altitude}".debug(this)
//        "temperatur: $min, $max with elevation: $elevation".debug(this)
    }

    fun getData(nama: String): CSVRecord? {
        if (!ecocrop.containsKey(nama)) {
            "Data untuk $nama tidak tersedia".warn(this)
            return null
//        throw Exception("Data untuk $nama tidak tersedia")
        }
        return ecocrop[nama]!!
    }

    fun getPlants() = plant.values

    companion object {
        private const val serialVersionUID = 3642316207630701856L
        fun newInstance(): PlantRepository {
            return PlantRepository()
        }
    }
}