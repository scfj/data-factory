package com.github.scfj.factory

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

class SimpleObjectsFactory<T : Any>(type: KClass<T>) : ObjectsFactory<T> {
    private var constructor: KFunction<T> = type.primaryConstructor
            ?: unsupported()

    private val attributes = mutableMapOf<String, () -> Any>()

    private var params: Map<String, KParameter> = constructor.parameters
            .map { param -> (param.name ?: unsupported()) to param }
            .toMap()

    override fun createInstance(vararg pairs: Pair<String, Any>): T {
        val defaultArguments = arguments().toMutableMap()
        val arguments = pairs.map { (name, value) ->
            (params[name] ?: noArgument()) to value
        }.toMap()
        return constructor.callBy(defaultArguments + arguments)
    }

    override fun attribute(
            attributeName: String,
            attributeSupplier: () -> Any
    ) {
        attributes[attributeName] = attributeSupplier
    }

    private fun arguments(): Map<KParameter, Any> {
        return constructor.parameters
                .map { param ->
                    param to (attributes[param.name] ?: noArgument())
                            .invoke()
                }
                .toMap()
    }

    override fun association(
            attributeName: String,
            factory: ObjectsFactory<*>
    ) {
        attributes[attributeName] = { factory.createInstance() }
    }

    private fun <T> noArgument(): T {
        throw IllegalArgumentException(
                "Please define all parameters from primary constructor"
        )
    }

    private fun <T> unsupported(): T {
        throw IllegalArgumentException(
                "Only Kotlin classes are supported"
        )
    }
}
