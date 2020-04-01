package com.github.scfj.factory

interface ObjectsFactory<T : Any> {
    fun createInstance(vararg pairs: Pair<String, Any>): T
    fun attribute(attributeName: String, attributeSupplier: () -> Any)
    fun association(attributeName: String, factory: ObjectsFactory<*>)
}
