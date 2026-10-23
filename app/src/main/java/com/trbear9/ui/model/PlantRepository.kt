package com.trbear9.ui.model

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.trbear9.plants.Constants
import com.trbear9.plants.Constants.Science_name
import com.trbear9.plants.TFService
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.util.debug
import com.trbear9.ui.util.info
import com.trbear9.ui.util.save
import java.io.Serializable
import android.content.Context
import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.core.StreamWriteConstraints
import com.fasterxml.jackson.databind.ObjectMapper
import com.openmeteo.api.Forecast
import com.openmeteo.api.OpenMeteo
import com.openmeteo.api.common.units.TemperatureUnit
import com.trbear9.plants.Constants.Category
import com.trbear9.plants.Constants.Common_names
import com.trbear9.plants.Evaluator.evaluateAllEcoCropPlants
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
import java.util.Locale.getDefault
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
                val scienceName = record[Science_name].lowercase(getDefault())
                plantRepository.ecocrop[scienceName] = record
                plantRepository.plant[scienceName]?.record = record
            }
        }
    }
    "Successfully Loaded ${plantRepository.ecocrop.size} plants".info("loadPlantRepository()")
}

fun findAssetFile(fileName: String): File {
    val candidates = listOf(
        File(fileName),
        File("src/main/assets", fileName),
        File("app/src/main/assets", fileName)
    )
    return candidates.firstOrNull { it.exists() }
        ?: File("src/main/assets", fileName).takeIf { File("src/main/assets").exists() }
        ?: File("app/src/main/assets", fileName).takeIf { File("app/src/main/assets").exists() }
        ?: File(fileName)
}

fun log(plant: Plant) {
    "Logging $plant".debug("PlantRepository")
    "   pH: ${plant.ph}"
}

fun streamEcoCrop(init: (CSVRecord) -> Unit) {
    findAssetFile("EcoCrop_DB.csv").inputStream().use { `is` ->
        InputStreamReader(`is`).use { reader ->
            for (record in CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build()
                .parse(reader)
                .records
            ) {
                init(record)
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
            findAssetFile("EcoCrop_DB.csv").inputStream().use { `is` ->
                InputStreamReader(`is`).use { reader ->
                    for (record in CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(reader)
                        .records
                    ) {
                        ecocrop[record[Science_name].lowercase(getDefault())] = record
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

        findAssetFile("KEW.json").inputStream().use {
            val type = object : TypeReference<Map<String, JsonNode>>() {}
            val k = objectMapper.readValue(it, type)
            for (entry in k) {
                val name = entry.key
                val ke = k[name]
                kew[name] = ke
            }
        }

        val plants = findAssetFile("plants.json").inputStream().use {
            objectMapper.readValue(it, Array<Plant>::class.java)
        }
        for (item in plants) plant[item.nama_ilmiah.lowercase(getDefault())] = item.also {
            val record = ecocrop[it.nama_ilmiah.lowercase(getDefault())] ?: run {
                "Cant find any related data about ${it.nama_ilmiah}".error(this)
                return@also
            }
            it.load(record)
            it.record = record
            it.isAvaiable = true
            "Loaded ${it.nama_ilmiah}".info(this)
        }
        evaluateAllEcoCropPlants()
        "Loaded ${ecocrop.size} ecocrop".debug(this)
        "Loaded ${kew.size} kew".debug(this)
        "Loaded ${plant.size} plants".debug(this)
        "Loaded ${namaUmumToNamaIlmiah.size} namaUmumToNamaIlmiah".debug(this)
        "Loaded ${namaIlmiahToNamaUmum.size} namaIlmiahToNamaUmum".debug(this)
        "Loaded ${tags.size} Tags size".debug(this)

        ecocrop.clear()
        DataSerializer.write(findAssetFile(REPOSITORY), this)
    }

    fun loadPlant(record: CSVRecord): Plant {
        val ilmiah = record[Science_name].lowercase()
//        if (plant.containsKey(ilmiah)) return plant[ilmiah]!!

//        "Loading $ilmiah".info(this)
        return if (plant[ilmiah] == null) {
//            "Full plant version of $ilmiah not found".warn(this)
            val plant = Plant(record)
            plant.load(record)
            plant
        } else {
//            val plant = plant [ilmiah]!!
//            if (plant.category.isEmpty()) "Cant find any category on plant $plant".info(this)
//            else "Loaded ${plant.category.size} categories from $plant".info(this)
            plant[ilmiah]!!
//            plant
        }
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
        } catch (_: Exception) {
            "Failed to get temperature".error(this)
        }
        response.geoParameterLoaded = true
        emit(response)
        "Successfully loaded geo parameter!".info(this)
        soil.let {
            it.texture = resultSoil!!.texture
            it.fertility = resultSoil.fertility
            it.drainage = resultSoil.drainage
            if (it.pH == null)
                it.pH = resultSoil.pH
        }
        val processedData = evaluateAllEcoCropPlants()
        for (i in processedData.keys)
            response.target += processedData[i]!!.size
        emit(response)
        for (i in processedData.keys) {
            for (plant in processedData[i]!!) {
                val namaIlmiah = plant.commonName
                "Loaded $namaIlmiah with score: $i".info(this)

                response.put(plant)
                response.progress++
                response.current = namaIlmiah
                emit(response)
            }
        }
        response.completed = true
        recommendationResult.collecting = false
        emit(response)
    }.flowOn(Dispatchers.Default)


    fun writeTaxonomy(plant: Plant) {
        plant.genus = plant.nama_ilmiah.split(" ")[0]
        if (kew.containsKey(plant.nama_ilmiah)) {
            plant.full_taxon = kew[plant.nama_ilmiah]
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