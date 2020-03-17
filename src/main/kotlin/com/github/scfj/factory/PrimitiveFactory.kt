package com.github.scfj.factory

import java.lang.reflect.Constructor
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object PrimitiveFactory {
    private val primitives: Map<Class<*>, () -> Any> = mapOf(
            String::class.java to { "" },
            List::class.java to { listOf<Any>() },
            Collection::class.java to { listOf<Any>() },
            MutableList::class.java to { mutableListOf<Any>() },
            MutableCollection::class.java to { mutableListOf<Any>() },
            Int::class.java to { 0 },
            Byte::class.java to { 0.toByte() },
            Char::class.java to { 0.toChar() },
            Long::class.java to { 0.toLong() },
            Short::class.java to { 0.toShort() },
            Float::class.java to { 0.toFloat() },
            Double::class.java to { 0.toDouble() },
            IntArray::class.java to { intArrayOf() },
            ByteArray::class.java to { byteArrayOf() },
            CharArray::class.java to { charArrayOf() },
            LongArray::class.java to { longArrayOf() },
            ShortArray::class.java to { shortArrayOf() },
            FloatArray::class.java to { floatArrayOf() },
            DoubleArray::class.java to { floatArrayOf() },
            Array<Any>::class.java to { arrayOf<Any>() }
    )

    private fun <T> createPrimitive(type: Class<T>): T? {
        if (primitives.containsKey(type)) {
            return primitives[type]?.invoke() as T
        }
        for ((t, v) in primitives) {
            if (type.isAssignableFrom(t)) {
                return v.invoke() as T
            }
        }
        return null
    }

    private fun <T> createObject(type: Class<T>): T {
        val ctor = type.constructors.simplest
                ?: throw IllegalArgumentException("Type $type must have at least one public ctor")
        val parameterTypes = ctor.parameterTypes
        val params = parameterTypes.map { create(it) }.toTypedArray()
        return ctor.newInstance(*params) as T
    }

    fun <T : Any> createObject(type: KClass<T>): T {
        val ctor = type.constructors.firstOrNull()
                ?: throw IllegalArgumentException("Type $type must have at least one public ctor")
        val params = ctor.parameters.map { it.type.classifier as KClass<*> }.map { createObject(it) }.toTypedArray()
        return ctor.call(*params)
    }


    private fun <T> createEnum(type: Class<T>): T? {
        if (type.isEnum) {
            return type.enumConstants.first()
        }
        return null
    }

    fun <T> create(type: Class<T>): T = createPrimitive(type) ?: createEnum(type) ?: createObject(type)

    fun <T : Any> create(type: KClass<T>): T = create(type.java)

    inline fun <reified T : Any> create(): T = create(T::class)

    private val Array<Constructor<*>>.simplest: Constructor<*>?
        get() = this.minBy { ctor -> ctor.parameterCount }
}
