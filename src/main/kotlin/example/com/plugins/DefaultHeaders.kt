package example.com.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*
import io.netty.handler.codec.DefaultHeaders


fun Application.configureDefaultHeaders() {
    install(DefaultHeaders) {
        val oneYearInSeconds = 3600
        header(name = HttpHeaders.CacheControl, value = "public, max-age=$oneYearInSeconds")
    }
}