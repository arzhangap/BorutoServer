package example.com.plugins

import example.com.model.ApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(HttpStatusCode.NotFound, message = ApiResponse(success = false, message = status.toString()))
        }
    }
}