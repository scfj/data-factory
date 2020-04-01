package com.github.scfj.factory

import org.junit.Test
import kotlin.test.assertEquals

class SomeFactoryTest {
    private val subject: ObjectsFactory<User> =
            SimpleObjectsFactory(User::class).apply {
                attribute("name") { "John" }
                attribute("age") { 28 }
            }

    @Test
    fun `should create simple object as factory defined`() {
        assertEquals(
                User("John", 28),
                subject.createInstance()
        )
    }

    @Test
    fun `params should override definition`() {
        assertEquals(
                User("Alice", 28),
                subject.createInstance("name" to "Alice")
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw an error when interface given`() {
        SimpleObjectsFactory(HasName::class).createInstance()
    }

    data class User(override val name: String, val age: Int): HasName

    interface HasName {
        val name: String
    }
}
