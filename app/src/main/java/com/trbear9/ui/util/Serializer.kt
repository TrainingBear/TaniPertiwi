package com.trbear9.ui.util

import java.io.File
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.ObjectStreamClass
import java.io.Serializable

interface SerializableProperties : Serializable

interface PropertySerializer {
    fun serialize(value: SerializableProperties): ByteArray
    fun <T : SerializableProperties> deserialize(
        bytes: ByteArray,
        clazz: Class<T>
    ): T
}

class AppObjectInputStream(
    inputStream: InputStream,
    private val classLoader: ClassLoader = Thread.currentThread().contextClassLoader ?: AppObjectInputStream::class.java.classLoader
) : ObjectInputStream(inputStream) {
    override fun resolveClass(desc: ObjectStreamClass): Class<*> {
        return try {
            Class.forName(desc.name, false, classLoader)
        } catch (_: ClassNotFoundException) {
            super.resolveClass(desc)
        }
    }
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

    fun <T : Serializable> read(
        file: File,
        classLoader: ClassLoader = Thread.currentThread().contextClassLoader ?: DataSerializer::class.java.classLoader
    ): T = read(AppObjectInputStream(file.inputStream().buffered(), classLoader))

    fun <T : Serializable> read(
        stream: InputStream,
        classLoader: ClassLoader = Thread.currentThread().contextClassLoader ?: DataSerializer::class.java.classLoader
    ): T = read(AppObjectInputStream(stream.buffered(), classLoader))
}

private val dir = File("serialized").also { it.mkdirs() }
fun save(name: String): File = File(name)
