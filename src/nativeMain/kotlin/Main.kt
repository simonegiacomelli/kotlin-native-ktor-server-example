import io.ktor.resources.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.serialization.Serializable

@Serializable
data class User(val id: Long, val name: String, val age: Int)

@Serializable
data class InsertUser(val name: String, val age: Int)

@Serializable
data class UpdateUser(val name: String? = null, val age: Int? = null)

@Resource("/users")
class Users() {
    @Resource("{id}")
    class Id(val parent: Users = Users(), val id: Long)
}

fun main() {
    println("Server is running at port 8080")
    embeddedServer(
        factory = CIO,
        port = 8080,
    ) {
        setupPlugins()
        setupRouter()
    }.start(wait = true)
    println("Server stopped")
}