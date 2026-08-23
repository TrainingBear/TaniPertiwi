package com.trbear9.plants


import android.content.Context
import android.graphics.Bitmap
import com.google.ai.edge.litert.CompiledModel
import com.trbear9.plants.parameters.SoilParameters
import androidx.core.graphics.scale
import com.trbear9.ui.util.estimate
import com.trbear9.ui.util.info

object TFService {
    //    val labels = listOf("Aluvial", "Andosol", "Humus", "Kapur", "Laterit", "Pasir")
    val labels =
        listOf("Aluvial", "Andosol", "Entisol", "Humus", "Inceptisol", "Laterit", "Kapur", "Pasir")

    //    val soils = mapOf(
//            "Aluvial" to SoilParameters.ALLUVIAL,
//            "Andosol" to SoilParameters.ANDOSOL,
//            "Humus" to SoilParameters.HUMUS,
//            "Kapur" to SoilParameters.KAPUR,
//            "Laterit" to SoilParameters.LATERITE,
//            "Pasir" to SoilParameters.PASIR)
    val soils = mapOf(
        "Aluvial" to SoilParameters.ALLUVIAL,
        "Andosol" to SoilParameters.ANDOSOL,
        "Humus" to SoilParameters.HUMUS,
        "Kapur" to SoilParameters.KAPUR,
        "Laterit" to SoilParameters.LATERITE,
        "Pasir" to SoilParameters.PASIR,
        "Inceptisol" to SoilParameters.INCEPTISOL,
        "Entisol" to SoilParameters.ENTISOL
    )
    var model: CompiledModel? = null


    fun load(context: Context) {
        model = CompiledModel.create(context.assets, "model.tflite")
    }

    fun predict(bitmap: Bitmap): FloatArray {
        // Preallocate input/output buffers
        return estimate(this, "Predicting Image") {
            val inputBuffers = model!!.createInputBuffers()
            val outputBuffers = model!!.createOutputBuffers()
            val floatArray = reprocessToFloatArray(bitmap)
            // Fill the first input
            inputBuffers[0].writeFloat(floatArray)
            // Invoke
            model!!.run(inputBuffers, outputBuffers)
            // Read the output
            val outputFloatArray = outputBuffers[0].readFloat()

            // Clean up buffers and model
            inputBuffers.forEach { it.close() }
            outputBuffers.forEach { it.close() }
            "soil prediction output: ${outputFloatArray.contentToString()}".info(this)
            outputFloatArray
        }
    }

    fun close() {
        model?.close()
    }

    fun argmax(prediction: FloatArray): Pair<String, Float> {
        var max = Float.MIN_VALUE
        var soil = ""
        for ((i, element) in prediction.withIndex()) {
            if (element > max) {
                max = element
                soil = labels[i]
            }
        }
        return Pair(soil, max)
    }

    fun getPredictions(prediction: FloatArray): List<Pair<String, Float>> {
        val predictions = mutableListOf<Pair<String, Float>>()
        for ((i, element) in prediction.withIndex()) {
            predictions.add(Pair(labels[i], element))
        }
        return predictions
    }

    fun reprocessToFloatArray(bitmap: Bitmap): FloatArray {
        val resized = bitmap.scale(320, 320)

        val pixels = IntArray(320 * 320)
        resized.getPixels(
            pixels,
            0,
            320,
            0,
            0,
            320,
            320
        )

        val output = FloatArray(320 * 320 * 3)

        var index = 0

        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF

            output[index++] = r / 255f
            output[index++] = g / 255f
            output[index++] = b / 255f
        }

        return output
    }
}