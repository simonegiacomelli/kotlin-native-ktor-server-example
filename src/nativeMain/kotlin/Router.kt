import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun Application.setupRouter() {
    val users = mutableListOf<User>()
    val mutex = Mutex()

    routing {
        get<Users> {
            call.respond(users)
        }
        get<Users.Id> { byId ->
            val user = try {
                users.first { it.id == byId.id }
            } catch (e: NoSuchElementException) {
                e.printStackTrace()
                call.respond(status = HttpStatusCode.NotFound, message = "User with ID ${byId.id} not found")
                return@get
            }
            call.respond(user)
        }
        post<Users> {
            val insertUser: InsertUser = call.receive()
            val id = mutex.withLock {
                val record = User(id = users.size + 1L, name = insertUser.name, age = insertUser.age)
                users.add(record)
                record.id
            }
            call.respond(status = HttpStatusCode.Created, message = "User with ID $id created")
        }
        put<Users.Id> { byId ->
            val insertOrUpdateUser: InsertUser = call.receive()
            val existingUser = try {
                users.first { it.id == byId.id }
            } catch (e: NoSuchElementException) {
                e.printStackTrace()
                val user = User(
                    id = byId.id,
                    name = insertOrUpdateUser.name,
                    age = insertOrUpdateUser.age,
                )
                mutex.withLock {
                    users.add(user)
                }
                call.respond(status = HttpStatusCode.Created, message = "User with ID ${byId.id} created")
                return@put
            }
            val updatedUser = User(
                id = existingUser.id,
                name = insertOrUpdateUser.name,
                age = insertOrUpdateUser.age,
            )
            mutex.withLock {
                users.remove(existingUser)
                users.add(updatedUser)
            }
            call.respond(status = HttpStatusCode.NoContent, message = "User with ID ${byId.id} updated")
        }
        patch<Users.Id> { byId ->
            val updateUser: UpdateUser = call.receive()
            val existingUser = try {
                users.first { it.id == byId.id }
            } catch (e: NoSuchElementException) {
                e.printStackTrace()
                call.respond(status = HttpStatusCode.NotFound, message = "User with ID ${byId.id} not found")
                return@patch
            }
            val updatedUser = User(
                id = byId.id,
                name = updateUser.name ?: existingUser.name,
                age = updateUser.age ?: existingUser.age,
            )
            mutex.withLock {
                users.remove(existingUser)
                users.add(updatedUser)
            }
            call.respond(status = HttpStatusCode.NoContent, message = "User with ID ${byId.id} updated")
        }
        delete<Users.Id> { byId ->
            if (mutex.withLock { users.removeAll { it.id == byId.id } }) {
                call.respond(status = HttpStatusCode.OK, message = "User with ID ${byId.id} deleted")
            } else {
                call.respond(status = HttpStatusCode.NotFound, message = "User with ID ${byId.id} not found")
            }
        }
    }
}