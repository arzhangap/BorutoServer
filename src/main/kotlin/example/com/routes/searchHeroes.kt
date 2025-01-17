package example.com.routes

import example.com.repository.HeroRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.searchHeroes() {
    val heroRepository: HeroRepository by inject()

    get("/boruto/heroes/search") {
        val query = call.request.queryParameters["query"]

        val response = heroRepository.searchHeroes(query)
        call.respond(HttpStatusCode.OK,response)
    }
}