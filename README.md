# Data Factory


## Usage:

```kotlin
data class User(val name: String, val age: Int)

val userFactory = ObjectsFactory<User>().apply {
    attribute("name") { "John" }
    attribute("age") { randomInt(20..40) }
}

val user0: User = create<User>()
val user1: User = create<User>(mapOf("age" to 90))
val user2: User = create<User>("age" to 90)
val user3: User = create<User>("name" to "John")
val user4: User = create<User>("name" to "Alice", "age" to 40)
```


