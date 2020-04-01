package com.github.scfj.factory

import org.junit.Test
import kotlin.test.assertEquals

class ComplexFactoryTest {
    private val userFactory: ObjectsFactory<User> =
            SimpleObjectsFactory(User::class).apply {
                attribute("name") { "John" }
                attribute("age") { 28 }
            }

    private val postFactory = SimpleObjectsFactory(Post::class).apply {
        attribute("content") { "Lorem ipsum" }
        association("author", userFactory)
    }

    @Test
    fun `should initialize association`() {
        assertEquals(
                Post("Lorem ipsum", User("John", 28)),
                postFactory.createInstance()
        )
    }

    data class User(val name: String, val age: Int)
    data class Post(val content: String, val author: User)
}
