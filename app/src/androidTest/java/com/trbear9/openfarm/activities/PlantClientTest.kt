package com.trbear9.ui.view

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.core.StreamWriteConstraints
import com.fasterxml.jackson.databind.ObjectMapper
import com.trbear9.plants.E.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

/**
 * OBSOLETE
 * IT IS NOW USING BUILTIN RECOMMENDATION MACHINE!!!!
 */
class PlantClientTest {
/*    val client = PlantClient("TrainingBear/84d0e105aaabce26c8dfbaff74b2280e", size = 200_000)
    val objectMapper = ObjectMapper()
    init {
            objectMapper.factory.setStreamReadConstraints(
                StreamReadConstraints.builder()
                    .maxStringLength(1_000_000)
                    .build()
            ).setStreamWriteConstraints(
                StreamWriteConstraints.builder()
                    .maxNestingDepth(1_000_000)
                    .build()
            )
    }

    @Test
    fun testBitmap() {
        ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun getPlant(){

    }
    @Test
    fun head(){
        runBlocking {
            val url = client.getUrl()
            Log.i("TEST","URL: $url")
        }
    }

    @Test
    fun get(){
        runBlocking {
            client.GET()
        }
    }*/
}