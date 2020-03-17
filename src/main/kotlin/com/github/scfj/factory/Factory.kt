package com.github.scfj.factory

import java.lang.reflect.Method
import kotlin.reflect.KClass

open class Factory<T : Any>(private val type: Class<T>) {
    private val attributeSpawn = mutableMapOf<String, (Int) -> Any?>()
    private var id = 1

    constructor(type: KClass<T>) : this(type.java)

    fun <F> field(attribute: String, function: (Int) -> F) {
        attributeSpawn[attribute] = function
    }

    fun <F : Any> association(attribute: String, factory: Factory<F>) {
        attributeSpawn[attribute] = { factory.create() }
    }

    fun create(): T {
        val obj = PrimitiveFactory.create(type)
        val setters = type.methods.filter { it.name.startsWith("set") }
        setters.forEach { setter ->
            val value = valueForSetter(setter)
            if (value != null) {
                setter.invoke(obj, value)
            }
        }
        id += 1
        return obj
    }

    private fun valueForSetter(setter: Method) =
            attributeSpawn[setter.attributeName]?.invoke(id)

    private val Method.attributeName: String
        get() = name.substringAfter("set").decapitalize()
}
