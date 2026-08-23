package com.trbear9.ui.util

import java.io.File
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable


interface SerializableProperties : Serializable

interface PropertySerializer {
    fun serialize(value: SerializableProperties): ByteArray
    fun <T : SerializableProperties> deserialize(
        bytes: ByteArray,
        clazz: Class<T>
    ): T
}

object DataSerializer {
    fun <T : Serializable> write(file: File, value: T) {
        ObjectOutputStream(file.outputStream().buffered()).use {
            it.writeObject(value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Serializable> read(stream: ObjectInputStream): T {
        return stream.use { it.readObject() as T }
    }
    fun <T : Serializable> read(file: File): T = read(ObjectInputStream(file.inputStream().buffered()))
    fun <T : Serializable> read(stream: InputStream): T = read(ObjectInputStream(stream.buffered()))
}

private val dir = File("serialized").also { it.mkdirs() }
fun save(name: String): File = File(name)
