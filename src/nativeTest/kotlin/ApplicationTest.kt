import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

fun ApplicationTestBuilder.createRestClient() = createClient {
    install(ContentNegotiation) {
        json(
            Json {
                isLenient = true
                explicitNulls = false
            }
        )
    }
}

class ApplicationTest {
    @Test
    fun testCreate() = testApplication {
        application {
            setupPlugins()
            setupRouter()
        }
        val client = createRestClient()

        // Insert user
        val insertionResponse = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(InsertUser(name = "Anand", age = 32))
        }
        assertEquals(HttpStatusCode.Created, insertionResponse.status)

        // Fetch user
        val fetchUserResponse = client.get("/users/1")
        assertEquals(HttpStatusCode.OK, fetchUserResponse.status)
        val user: User = fetchUserResponse.body()
        assertEquals(User(id = 1, name = "Anand", age = 32), user)
    }

    @Test
    fun testRead() = testApplication {
        application {
            setupPlugins()
            setupRouter()
        }
        val client = createRestClient()

        // Insert user
        val insertionResponse = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(InsertUser(name = "Anand", age = 32))
        }
        assertEquals(HttpStatusCode.Created, insertionResponse.status)

        // Fetch all users
        val fetchUsersResponse = client.get("/users")
        assertEquals(HttpStatusCode.OK, fetchUsersResponse.status)
        val users: List<User> = fetchUsersResponse.body()
        assertEquals(listOf(User(id = 1, name = "Anand", age = 32)), users)
    }

    @Test
    fun testUpdateWithPutMethod() = testApplication {
        application {
            setupPlugins()
            setupRouter()
        }
        val client = createRestClient()

        // Insert user
        val insertionResponse = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(InsertUser(name = "Anand", age = 32))
        }
        assertEquals(HttpStatusCode.Created, insertionResponse.status)

        // Fetch user
        val fetchUserResponse = client.get("/users/1")
        assertEquals(HttpStatusCode.OK, fetchUserResponse.status)
        val user: User = fetchUserResponse.body()
        assertEquals(User(id = 1, name = "Anand", age = 32), user)

        // Update user
        val updateUserResponse = client.put("/users/1") {
            contentType(ContentType.Application.Json)
            setBody(UpdateUser(name = "Anand Bose", age = 33))
        }
        assertEquals(HttpStatusCode.NoContent, updateUserResponse.status)

        // Fetch user again
        val fetchUpdatedUserResponse = client.get("/users/1")
        assertEquals(HttpStatusCode.OK, fetchUpdatedUserResponse.status)
        val updateUser: User = fetchUpdatedUserResponse.body()
        assertEquals(User(id = 1, name = "Anand Bose", age = 33), updateUser)
    }

    @Test
    fun testUpdateWithPatchMethod() = testApplication {
        application {
            setupPlugins()
            setupRouter()
        }
        val client = createRestClient()

        // Insert user
        val insertionResponse = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(InsertUser(name = "Anand", age = 32))
        }
        assertEquals(HttpStatusCode.Created, insertionResponse.status)

        // Fetch user
        val fetchUserResponse = client.get("/users/1")
        assertEquals(HttpStatusCode.OK, fetchUserResponse.status)
        val user: User = fetchUserResponse.body()
        assertEquals(User(id = 1, name = "Anand", age = 32), user)

        // Update user
        val updateUserResponse = client.patch("/users/1") {
            contentType(ContentType.Application.Json)
            setBody(UpdateUser(age = 33))
        }
        assertEquals(HttpStatusCode.NoContent, updateUserResponse.status)

        // Fetch user again
        val fetchUpdatedUserResponse = client.get("/users/1")
        assertEquals(HttpStatusCode.OK, fetchUpdatedUserResponse.status)
        val updateUser: User = fetchUpdatedUserResponse.body()
        assertEquals(User(id = 1, name = "Anand", age = 33), updateUser)
    }

    @Test
    fun deleteUser() = testApplication {
        application {
            setupPlugins()
            setupRouter()
        }
        val client = createRestClient()

        // Insert user
        val insertionResponse = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(InsertUser(name = "Anand", age = 32))
        }
        assertEquals(HttpStatusCode.Created, insertionResponse.status)

        // Fetch all users
        val fetchUsersResponse = client.get("/users")
        assertEquals(HttpStatusCode.OK, fetchUsersResponse.status)
        val users: List<User> = fetchUsersResponse.body()
        assertEquals(listOf(User(id = 1, name = "Anand", age = 32)), users)

        // Delete user
        val deletionResponse = client.delete("/users/1")
        assertEquals(HttpStatusCode.OK, deletionResponse.status)

        // Fetch all users
        val fetchUsersPostDeletionResponse = client.get("/users")
        assertEquals(HttpStatusCode.OK, fetchUsersPostDeletionResponse.status)
        val usersPostDeletion: List<User> = fetchUsersPostDeletionResponse.body()
        assertEquals(emptyList(), usersPostDeletion)
    }
}