package com.trbear9.plants

import com.trbear9.plants.parameters.UserVariable
import com.trbear9.ui.util.splitCommas
import org.apache.commons.csv.CSVRecord
import kotlin.math.absoluteValue
import kotlin.math.max

fun evaluateAll(record: CSVRecord, input: UserVariable) : Int{
    val constraints = listOf(
        RainEval,
        TemperatureEval,
        CycleEval,
        TextureEval,
        PhEval,
        DrainageEval,
        AltitudeEval,
        ClimateEval,
        LatitudeEval
    )
    var score = 0;
    for (constraint in constraints) {
        try {
            val temp =constraint.evaluate(record, input)
//            "    ${constraint::class.simpleName}: $temp".info("Csv");
            score += temp;
        } catch (e: Exception){
//            "Can't evaluate ${record.get(E.Science_name)} with constraint of ${constraint::class.simpleName}".warn("CsvHandler")
        }
    }
    return score;
}

object RainEval : Constraint {
    override fun evaluate(
        record: CSVRecord,
        input: UserVariable
    ): Int {
        val min = record.get(E.A_minimum_rainfall).toFloat()
        val max = record.get(E.A_maximum_rainfall).toFloat()
        return if (input.geo.rainfall in min..max) 1 else {
            input.geo.rainfall.coerceIn(
                Math.min(min, max),
                Math.max(min, max)
            ).absoluteValue.toInt() * -1 / 100;
        }
    }
}

object TemperatureEval : Constraint {
    override fun evaluate(
        record: CSVRecord,
        input: UserVariable
    ): Int {
        val mn = record.get(E.A_minimum_temperature).toFloat()
        val mx = record.get(E.A_maximum_temperature).toFloat()
        return if (input.geo.min >= mn && input.geo.max <= mx) 2 else {
            (max(0f, mn - input.geo.min) + max(input.geo.max - mx, 0f)).toInt() * -1 /2;
        }
    }
}

object CycleEval : Constraint {
    override fun evaluate(
        record: CSVRecord,
        input: UserVariable
    ): Int {
        input.custom?:return 0;
        val mn = record.get(E.MIN_crop_cycle).toInt()
        val mx = record.get(E.MAX_crop_cycle).toInt()
        return if (input.custom?.panen in mn..mx) 100 else {
            -100
        }
    }
}

object TextureEval : Constraint {
    override fun evaluate(
        record: CSVRecord,
        input: UserVariable
    ): Int {
        var score = 0;

        if (input.soil.texture == E.TEXTURE.wide) score += 2;
        else {
            var flag = false;
            if (record.get(E.A_soil_texture).splitCommas().contains(input.soil.texture?.head)) {
                score++
                flag = true;
            };
            if (!flag) {
                if (record.get(E.O_soil_texture).splitCommas()
                        .contains(input.soil.texture?.head)
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
        record: CSVRecord,
        input: UserVariable
    ): Int {
        val mnPh = record.get(E.A_minimum_ph).toFloat()
        val mxPh = record.get(E.A_maximum_ph).toFloat()
        return if ((input.soil.pH ?: 0f) in mnPh..mxPh) 1;
        else {
            val absoluteValue = input.soil.pH?.coerceIn(
                Math.min(mnPh, mxPh),
                Math.max(mnPh, mxPh)
            )?.absoluteValue ?: 2f;
            (absoluteValue * -1).toInt()
        }
    }
}

object DrainageEval : Constraint {
    override fun evaluate(
        record: CSVRecord,
        input: UserVariable
    ): Int {
        return if (record.get(E.A_soil_drainage).splitCommas().contains(input.soil.drainage?.head)) 1
        else -2
    }
 }

object AltitudeEval : Constraint {
    override fun evaluate(
        record: CSVRecord,
        input: UserVariable
    ): Int {
        val mxAlt = record.get(E.O_maximum_altitude).toFloat()
        return if(input.geo.altitude <= mxAlt) 1;
        else -1;
    }
}
object ClimateEval : Constraint {
    override fun evaluate(
        record: CSVRecord,
        input: UserVariable
    ): Int {
        var score = 0;
        if(record.get(E.Climate_zone).splitCommas().contains(input.geo.iklim?.head)) score++;
        else score-=354
        return score
    }
}
object LatitudeEval : Constraint {
    override fun evaluate(
        record: CSVRecord,
        input: UserVariable
    ): Int {
        val mnLat = record.get(E.A_minimum_latitude).toFloat()
        val mxLat = record.get(E.A_maximum_latitude).toFloat()
        return if (input.geo.latitude in mnLat..mxLat) 1;
        else {
            input.geo.latitude.toFloat().coerceIn(
                Math.min(mnLat, mxLat),
                Math.max(mnLat, mxLat)
            ).absoluteValue.toInt()*-1;
        }
    }
}