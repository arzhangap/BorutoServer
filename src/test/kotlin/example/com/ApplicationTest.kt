package example.com

import example.com.di.koinModule
import example.com.model.ApiResponse
import example.com.model.Hero
import example.com.repository.HeroRepository
import example.com.repository.HeroRepositoryImpl
import example.com.repository.NEXT_PAGE_KEY
import example.com.repository.PREV_PAGE_KEY
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import org.koin.test.KoinTest
import kotlin.math.exp
import kotlin.test.*

class ApplicationTest : KoinTest {
    @Before
    fun setUpKoin() {
        startKoin {
            modules(
                koinModule
            )
        }
    }

    @After
    fun teardown() = stopKoin()

    private val heroRepository: HeroRepository by inject(HeroRepository::class.java)

    @Test
    fun testRoot() = testApplication {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(
                "Hello World!",
                bodyAsText()
            )
        }
    }

    @Test
    fun accessHeroesEndpoint_AssertCorrectInformation() = testApplication {
        val pages = 1..5
        val heroes = listOf(
            heroRepository.page1,
            heroRepository.page2,
            heroRepository.page3,
            heroRepository.page4,
            heroRepository.page5,
        )
        pages.forEach { page ->
            client.get("/boruto/heroes?page=$page").apply {
                assertEquals(HttpStatusCode.OK, status)

                val actual = Json.decodeFromString<ApiResponse>(bodyAsText())

                val expected = ApiResponse(
                    success = true,
                    message = "ok",
                    prevPage = calculatePage(page = page)["prevPage"],
                    nextPage = calculatePage(page = page)["nextPage"],
                    heroes = heroes[page - 1],
                    lastUpdated = actual.lastUpdated
                )

                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun accessNotExistingEndpoint_AssertError() = testApplication {
        client.get("/boruto/heroes?page=6").apply {
            assertEquals(HttpStatusCode.NotFound, status)

            val expected = ApiResponse(
                success = false,
                message = "404 Not Found"
            )

            print(bodyAsText())
            val actual = Json.decodeFromString<ApiResponse>(bodyAsText())

            assertEquals(expected, actual)
        }
    }

    @Test
    fun accessHeroesEndpointWithInvalidParameter_AssertError() = testApplication {
        client.get("/boruto/heroes?page=Invalid").apply {
            assertEquals(HttpStatusCode.BadRequest, status)

            val expected = ApiResponse(
                success = false,
                message = "Only Numbers are Allowed.")

            val actual = Json.decodeFromString<ApiResponse>(bodyAsText())

            assertEquals(expected, actual)
        }
    }

    @Test
    fun accessHeroSearchEndpoint_AssertCorrectInformation() = testApplication {
        client.get("/boruto/heroes/search?query=Sasuke").apply {
            assertEquals(HttpStatusCode.OK, status)

            val expected = heroRepository.searchHeroes("Sasuke")

            val actual = Json.decodeFromString<ApiResponse>(bodyAsText())

            assertEquals(expected, actual)
        }
    }

    @Test
    fun accessHeroSearchEndpoint_AssertNotFound() = testApplication {
        client.get("/boruto/heroes/search?query=Unknown").apply {
            assertEquals(HttpStatusCode.OK, status)

            val actual = Json.decodeFromString<ApiResponse>(bodyAsText()).heroes

            assertEquals(expected = emptyList(), actual = actual)
        }
    }

    private fun calculatePage(page: Int): Map<String, Int?> {
        val prevPage: Int? = if (page in 2..5) page - 1 else null
        val nextPage: Int? = if (page in 1..4) page + 1 else null
        return mapOf(PREV_PAGE_KEY to prevPage, NEXT_PAGE_KEY to nextPage)
    }
}

