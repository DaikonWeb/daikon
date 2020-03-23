package daikon

import daikon.core.HttpStatus.INTERNAL_SERVER_ERROR_500
import daikon.core.HttpStatus.OK_200
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import topinambur.http

class WelcomePageTest {

    @Test
    fun `rendered on root`() {
        HttpServer().start().use {
            val response = local("/").http.get()
            assertThat(response.statusCode).isEqualTo(OK_200)
        }
    }

    @Test
    fun `override welcome page`() {
        HttpServer {
            get("/") { _, res -> res.status(INTERNAL_SERVER_ERROR_500) }
        }.start().use {
            val response = local("/").http.get()
            assertThat(response.statusCode).isEqualTo(INTERNAL_SERVER_ERROR_500)
        }
    }
}