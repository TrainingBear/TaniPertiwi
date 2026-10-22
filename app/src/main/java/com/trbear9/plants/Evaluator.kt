package com.trbear9.plants

import com.trbear9.plants.metadata_enumerates.Texture
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.model.plantRepository
import com.trbear9.ui.util.debug
import com.trbear9.ui.util.info
import com.trbear9.ui.util.splitCommas
import com.trbear9.ui.viewmodel.custom
import com.trbear9.ui.viewmodel.geo
import com.trbear9.ui.viewmodel.soil
import org.apache.commons.csv.CSVRecord
import java.util.TreeMap
import kotlin.math.absoluteValue
import kotlin.math.max

object Evaluator {
    fun evaluateAllPerfectPlants(): TreeMap<Int, MutableSet<Plant>> {
        if (plantRepository.plant.isEmpty()) throw Exception("Cant find any plants. this is a bug, please contact kukuhsudrajad354@gmail.com for resolve")
        val processedData = TreeMap<Int, MutableSet<Plant>>()

        var cnt = 0
        var scanned = 0;
        for ((_, plant) in plantRepository.plant) {
            scanned++;
            val score = evaluate(plant)
//            if (score < 0) "evaluated '${record.get(E.Science_name)}' with score: $score".info(this)
            if (plant.authored) {
                processedData.computeIfAbsent(score) { HashSet() }
                    .add(plant)
                if (plant.isAvaiable) cnt++;
            }
        }
        "$scanned plants successfully filtered with $cnt plants are accepted!".info(this)
        return processedData
    }

    fun evaluateAllEcoCropPlants(): TreeMap<Int, MutableSet<Plant>> {
        if (plantRepository.ecocrop.isEmpty()) throw Exception("EcoCrop_DB.csv not found, coba untuk load()")
        val processedData = TreeMap<Int, MutableSet<Plant>>()

        var cnt = 0
        var cat = 0;
        var scanned = 0;
        for ((name, record) in plantRepository.ecocrop) {
            val plant = plantRepository.loadPlant(record)
            scanned++;
            val score = evaluate(plant)
//            if (score < 0) "evaluated '${record.get(E.Science_name)}' with score: $score".info(this)
            if (plant.authored && score > 0) {
                processedData.computeIfAbsent(score) { HashSet() }
                    .add(plant)
                if (plant.isAvaiable) cnt++;
                if (!plant.category.isEmpty()) {
                    cat++
                }
            }
        }
        "$scanned plants successfully filtered with $cnt plants are accepted! ($cat are defined cateogry)".info(
            this
        )
        return processedData
    }

    fun evaluate(plant: Plant): Int {
        val record = plant.record!!
        val constraints = listOf(
            RainEval,
            TemperatureEval,
//            CycleEval,
            TextureEval,
            PhEval,
            DrainageEval,
            AltitudeEval,
            ClimateEval,
            LatitudeEval
        )
        val result = IntArray(constraints.size)
        var score = 0;
        for (i in constraints.indices) {
            val constraint = constraints[i]
            try {
                val d = constraint.evaluate(record)
                result[i] = d
//            "    ${constraint::class.simpleName}: $temp".info("Csv");
                score += d;
            } catch (e: Exception) {
//            "Can't evaluate ${record.get(E.Science_name)} with constraint of ${constraint::class.simpleName}".warn("CsvHandler")
            }
        }
        if (score <= 0 && plant.isAvaiable) {
            "Couldnt evaluate ${plant.commonName} with total score $score. heres what missing:".debug(
                this
            )
            for (i in result.indices) {
                val d = result[i]
                val constraint = constraints[i]
                if (d <= 0)
                    " - ${constraint::class.qualifiedName} -> $d".debug(this)
            }
        }
        return score;
    }
}

object RainEval : Constraint {
    override fun evaluate(
        record: CSVRecord
    ): Int {
        val min = record.get(Constants.A_minimum_rainfall).toFloat()
        val max = record.get(Constants.A_maximum_rainfall).toFloat()
        return if (geo.rainfall in min..max) 1 else {
            geo.rainfall.coerceIn(
                Math.min(min, max),
                Math.max(min, max)
            ).absoluteValue.toInt() * -1 / 100;
        }
    }
}

object TemperatureEval : Constraint {
    override fun evaluate(
        record: CSVRecord
    ): Int {
        val mn = record.get(Constants.A_minimum_temperature).toFloat()
        val mx = record.get(Constants.A_maximum_temperature).toFloat()
        return if (geo.min >= mn && geo.max <= mx) 2 else {
            (max(0f, mn - geo.min) + max(geo.max - mx, 0f)).toInt() * -1 / 2;
        }
    }
}

object CycleEval : Constraint {
    override fun evaluate(
        record: CSVRecord
    ): Int {
        custom ?: return 0;
        val mn = record.get(Constants.MIN_crop_cycle).toInt()
        val mx = record.get(Constants.MAX_crop_cycle).toInt()
        return if (custom?.panen in mn..mx) 100 else {
            -100
        }
    }
}

object TextureEval : Constraint {
    override fun evaluate(
        record: CSVRecord
    ): Int {
        var score = 0;

        if (soil.texture == Texture.wide) score += 2;
        else {
            var flag = false;
            if (record.get(Constants.A_soil_texture).splitCommas().contains(soil.texture?.origin)) {
                score++
                flag = true;
            };
            if (!flag) {
                if (record.get(Constants.O_soil_texture).splitCommas()
                        .contains(soil.texture?.origin)
                ) {
                    score++
                    flag = true;
                }
            };
            if (!flag) score--;
        }
        return score
    };
}

object PhEval : Constraint {
    override fun evaluate(
        record: CSVRecord
    ): Int {
        val mnPh = record.get(Constants.A_minimum_ph).toFloat()
        val mxPh = record.get(Constants.A_maximum_ph).toFloat()
        return if ((soil.pH ?: 0f) in mnPh..mxPh) 1;
        else {
            val absoluteValue = soil.pH?.coerceIn(
                Math.min(mnPh, mxPh),
                Math.max(mnPh, mxPh)
            )?.absoluteValue ?: 2f;
            (absoluteValue * -1).toInt()
        }
    }
}

object DrainageEval : Constraint {
    override fun evaluate(
        record: CSVRecord
    ): Int {
        return if (record.get(Constants.A_soil_drainage).splitCommas()
                .contains(soil.drainage?.origin)
        ) 1
        else -2
    }
}

object AltitudeEval : Constraint {
    override fun evaluate(
        record: CSVRecord
    ): Int {
        val mxAlt = record.get(Constants.O_maximum_altitude).toFloat()
        return if (geo.altitude <= mxAlt) 1;
        else -1;
    }
}

object ClimateEval : Constraint {
    override fun evaluate(
        record: CSVRecord
    ): Int {
        var score = 0;
        if (record.get(Constants.Climate_zone).splitCommas().contains(geo.iklim?.origin)) score++;
        else score -= 354
        return score
    }
}

object LatitudeEval : Constraint {
    override fun evaluate(
        record: CSVRecord
    ): Int {

        val mnRaw = record.get(Constants.A_minimum_latitude)
        val mxRaw = record.get(Constants.A_maximum_latitude)

        // Handle NA
        if (mnRaw == "NA" || mxRaw == "NA") return 0

        val mnLat = mnRaw.toFloat().absoluteValue
        val mxLat = mxRaw.toFloat().absoluteValue

        val min = minOf(mnLat, mxLat)
        val max = maxOf(mnLat, mxLat)

        val userLat = geo.latitude.absoluteValue.toFloat()

        return if (userLat in min..max) {
            1
        } else {
            val distance = when {
                userLat < min -> min - userLat
                userLat > max -> userLat - max
                else -> 0f
            }
            -distance.toInt()
        }
    }
}