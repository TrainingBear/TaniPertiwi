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
}