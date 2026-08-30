package com.trbear9.ui.util

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.trbear9.plants.metadata_enumerates.Category
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

fun String.info(tag: Any) = info(tag::class.java.simpleName)
fun String.info(tag: String) = try {
    Log.i(tag, this)
} catch (_: Exception) {
    println(this)
}

fun String.debug(tag: Any) = debug(tag::class.java.simpleName)
fun String.debug(tag: String) = try {
    Log.d(tag, this)
} catch (_: Exception) {
    println(this)
}

fun String.warn(tag: Any) = warn(tag::class.java.simpleName)
fun String.warn(tag: String) = try {
    Log.w(tag, this)
} catch (_: Exception) {
    println(this)
}

fun String.error(tag: Any) = error(tag::class.java.simpleName)
fun String.error(tag: String) = try {
    Log.e(tag, this)
} catch (_: Exception) {
    println(this)
}

@Composable
fun getContext(): Context = LocalContext.current

fun String.splitCommas(): List<String> {
    val list = mutableListOf<String>()
    this.split(", ").forEach {
        it.split(",").forEach { final -> list += final }
    }
    return list
}

fun <T: Any> estimate(source: Any, message: String = "", task: () -> T) : T{
    val start = System.currentTimeMillis()
    val result = task()
    "Executing $task took ${(System.currentTimeMillis()-start)/1_000} ms!".info(source)
    return result
}

object Util {
    fun debug(message: String) {
        Log.d("OpenFarm", message)
    }

    @Throws(IOException::class)
    fun loadModelFile(assets: AssetManager, modelFilename: String): MappedByteBuffer? {
        val fileDescriptor = assets.openFd(modelFilename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun diffToColor(diff: String): Color {
        return when (diff) {
            "EASY" -> Color.Green
            "MEDIUM" -> Color.Yellow
            "HARD" -> Color.Red
            else -> Color.Gray
        }
    }

    fun scoreToColor(score: Int): Color {
        return when (score) {
            in 0..2 -> Color.Red
            in 3..6 -> Color.Yellow
            in 7..354 -> Color.Green
            else -> Color.Blue
        }
    }

    fun categoryToColor(category: String): Color {
        return when (category) {
            Category.OTHER.origin -> Color.Gray
            Category.VEGETABLES.origin -> Color.Green
            Category.CEREALS_PSEUDOCEREALS.origin -> Color(0xFF4CAF50)
            Category.ROOTS_TUBERS.origin -> Color.Red
            Category.FORAGE_PASTURES.origin -> Color.Blue
            Category.FRUIT_NUT.origin -> Color(0xFF9C27B0)
            Category.MATERIALS.origin -> Color(0xFFFF9100)
            Category.ORNAMENTALS_TURF.origin -> Color(0xFFE91E63)
            Category.MEDICINALS_AND_ARMOATIC.origin -> Color(0xFF936123)
            else -> Color(0xFF9E9E9E)
        }
    }

    fun bulkCategoryTranslate(categories: Set<String>): String? {
        var ans = "";
        categories.forEach {
            ans += translateCategory(it) + ", "
        }
        ans.trimEnd { it == ',' }
        return ans
    }

    fun bulkCategoryTranslate(categories: String?): String? {
        categories ?: return null;
        var ans = "";
        categories.splitCommas().forEach {
            ans += translateCategory(it) + ", "
        }
        return ans;
    }

    fun bulkClimateTranslate(climates: String?): String? {
        climates ?: return null;
        var ans = "";
        climates.splitCommas().forEach {
            ans += translateClimate(it) + ", "
        }
        return ans;
    }

    @Deprecated("use Category translation constant instead", level = DeprecationLevel.WARNING)
    fun translateCategory(category: String): String {
        when (category) {
            Category.OTHER.origin -> return "Lainnya"
            Category.VEGETABLES.origin -> return "Sayur"
            Category.CEREALS_PSEUDOCEREALS.origin -> return "Pseudocereal"
            Category.ROOTS_TUBERS.origin -> return "Akar/Umbi"
            Category.FORAGE_PASTURES.origin -> return "Pakan ternak"
            Category.FRUIT_NUT.origin -> return "Buah & kacang"
            Category.MATERIALS.origin -> return "Bahan"
            Category.ORNAMENTALS_TURF.origin -> return "Rumput hias"
            Category.MEDICINALS_AND_ARMOATIC.origin -> return "Obat & aromatik"
            Category.FOREST_OR_WOOD.origin -> return "Hutan/Kayu"
            Category.COVER_CROP.origin -> return "Tanaman penutup"
            Category.ENVIRONMENTAL.origin -> return "Lingkungan"
            Category.WEED.origin -> return "Gulma"
        }
        return "Lainnya"
    }

    @Deprecated("use Category translation constant instead", level = DeprecationLevel.WARNING)
    fun translateClimate(climate: String): String {
        return when (climate) {
            "tropical wet & dry (Aw)" -> "tropis basah & kering (Aw)"
            "tropical wet (Ar)" -> "tropis kering"
            "steppe or semiarid (Bs)" -> "steppe or semiarid (Bs)" // flag
            "subtropical humid (Cf)" -> "subtropis basah"
            "subtropical dry summer (Cs)" -> "mediterania"
            "subtropical dry winter (Cw)" -> "subtropis lembab"
            "temperate oceanic (Do)" -> "maritim"
            "temperate continental (Dc)" -> "temperate continental (Dc)"
            "temperate with humid winters (Df)" -> "temperate with humid winters (Df)"
            "temperate with dry winters (Dw)" -> "dingin"
            "desert or arid (Bw)" -> "gurun"
            "boreal (E)" -> "taiga"
            "polar (F)" -> "kutub"
            else -> ""
        }
    }

    fun getCategory(): List<String> {
        return Category.entries.map { it.translation }
    }
}
