package example.com.plugins

import example.com.routes.searchHeroes
import example.com.routes.getAllHeroes
import example.com.routes.root
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        root()
        getAllHeroes()
        searchHeroes()
        staticResources("images", "images")
    }
}
