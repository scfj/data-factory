package com.github.scfj.factory

import kotlin.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

class KFactory<T : Any>(private val type: KClass<T>) {
    private val ctorArguments = mutableMapOf<String, () -> Any>()

    fun field(attribute: String, init: () -> Any) {
        ctorArguments[attribute] = init
    }

    fun attribute(attribute: String, init: () -> Any) = field(attribute, init)

    fun association(attribute: String, factory: KFactory<*>) =
            field(attribute, factory::create)

    fun create(): T = ctor.callBy(argumentsByName())

    private val ctor: KFunction<T>
        get() = bestCtor ?: noConstructorError()

    private fun argumentsByName() =
            ctor.parameters
                    .map { param -> param to ctorArguments[param.name]?.invoke() }
                    .toMap()

    private val bestCtor: KFunction<T>?
        get() {
            val primary = type.primaryConstructor
            if (canCall(primary)) {
                return primary
            }
            val callable = type.constructors.filter { ctor -> canCall(ctor) }
            val best = callable.find { ctor -> ctor.parameters.size == ctorArguments.size }
            return best ?: callable.firstOrNull()
        }

    private fun canCall(ctor: KFunction<T>?) =
            ctor != null && ctor.parameters.all { param -> param.hasArgument() }

    private fun KParameter.hasArgument() = ctorArguments.containsKey(this.name)

    private fun <E : Any> noConstructorError(): E {
        throw IllegalArgumentException(
                "$type have no constructor that can be invoked with arguments ${ctorArguments.keys}"
        )
    }
}