package com.trbear9.plants

import android.content.Context
import com.trbear9.ui.util.error
import com.trbear9.plants.parameters.Parameters
import com.trbear9.plants.parameters.UserVariable
import com.trbear9.ui.util.info
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.ObjectOutputStream
import java.util.*
import kotlin.math.abs
import kotlin.math.absoluteValue

private val dir = File("serialized").also{it.mkdirs()}
fun save(name: String): File = File(dir, name)

object CsvHandler {
    @JvmField
    var explored_fields: Int = 0
    var ecocropcsv: MutableList<CSVRecord>? = null
    var perawatan: MutableList<CSVRecord>? = null

    fun preload(){
        try {
            File("EcoCrop_DB.csv").inputStream().use { `is` ->
                InputStreamReader(`is`).use {
                        reader -> ecocropcsv = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).records
                }
            }

            File("Perawatan.csv").inputStream().use { `is` ->
                InputStreamReader(`is`).use {
                        reader -> perawatan = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).records
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
        ObjectOutputStream(save("ecocropcsv.ser").outputStream()).use {
            it.writeObject(ecocropcsv)
        }
        ObjectOutputStream(save("perawatan.ser").outputStream()).use{
            it.writeObject(perawatan)
        }
    }

    fun load(context: Context) {
//        context.assets.list("serialized/CSV")?.forEach {
//            val field = this::class.java.getDeclaredField(it.replace(".ser", ""))
//            field.isAccessible = true
//
//            field.set(this, ObjectInputStream(
//                context.assets.open("serialized/CSV/$it")
//            ).readObject())
//
//            "Deserialized $it".info("CsvHandler")
//        }
        try {
            context.assets.open("EcoCrop_DB.csv").use { `is` ->
                InputStreamReader(`is`).use {
                        reader -> ecocropcsv = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).records
                }
            }

            context.assets.open("Perawatan.csv").use { `is` ->
                InputStreamReader(`is`).use {
                        reader -> perawatan = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).records
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    fun isAuthored(record: CSVRecord): Boolean {
        return record.get(E.Authority) != null || !record.get(E.Authority).isEmpty()
    }

    @JvmStatic
    fun process(data: UserVariable): MutableMap<Int, MutableSet<CSVRecord>> {
        val map: MutableMap<Int, MutableSet<CSVRecord>> =
            TreeMap<Int, MutableSet<CSVRecord>>()
        if(ecocropcsv==null){
            throw Exception("EcoCrop_DB.csv not found, coba untuk load()")
        }

        for (record in ecocropcsv) {
            explored_fields++
            val score = evaluateAll(record, data)
            if(score < 0) "${record.get(E.Science_name)} score: $score".info("Csv")

            if (isAuthored(record) && score > 0)
                map.computeIfAbsent(score) { k: Int? -> HashSet<CSVRecord>() }
                .add(record)
        }
        return map
    }

    fun perawatan(record: CSVRecord): CSVRecord? {
        if(perawatan==null){
            throw Exception("Perawatan.csv is not loaded! tolong load() sebelum digunakan")
        }
        for (i in perawatan) {
            val s = i.get(record.get(E.Science_name))
            if (s != null) return i
        }
        return null
    }

    fun qperawatan(record: CSVRecord): String {
        return record.get(E.PERAWATAN)
    }

    fun qpenyakit(record: CSVRecord): String {
        return record.get(E.PENYAKIT)
    }

    @JvmStatic
    fun getRecord(query: String, column: String?): CSVRecord? {
        if(ecocropcsv==null){
            throw Exception("Ecocrop.csv is not loaded! tolong load() sebelum digunakan")
        }
        for (i in ecocropcsv) {
//            log.info("checking {}", i.get(column));
            if (i.get(column).contains(query)) {
                return i
            }
        }
        return null
    }

    private val science_perawatancsv: MutableMap<String?, CSVRecord?> = HashMap<String?, CSVRecord?>()
    fun perawatanCsv(ecocropcsv: CSVRecord): CSVRecord? {
        if(perawatan==null){
            throw Exception("Perawatan.csv is not loaded! tolong load() sebelum digunakan")
        }
        if (science_perawatancsv.containsKey(ecocropcsv.get(E.Science_name)))
            return science_perawatancsv[ecocropcsv[E.Science_name]]
        for (i in perawatan) {
            if (i.get(E.Science_name).contains(ecocropcsv.get(E.Science_name))) {
                science_perawatancsv.put(ecocropcsv.get(E.Science_name), i)
                return i
            }
        }
        return null
    }
}
