import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.*
import kotlinx.serialization.json.Json

fun Application.setupPlugins() {
    install(Resources)
    install(ContentNegotiation) {
        json(
            Json {
                explicitNulls = false
                isLenient = true
            }
        )
    }
}