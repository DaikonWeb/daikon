package daikon

import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jetty.http.HttpStatus.INTERNAL_SERVER_ERROR_500
import org.eclipse.jetty.http.HttpStatus.OK_200
import org.junit.jupiter.api.Test

class WelcomePageTest {

    @Test
    fun `rendered on root`() {
        HttpServer().start().use {
            val response = Localhost.get("/")
            assertThat(response.statusCode).isEqualTo(OK_200)
        }
    }

    @Test
    fun `override welcome page`() {
        HttpServer {
            get("/") { _, res -> res.status(INTERNAL_SERVER_ERROR_500) }
        }.start().use {
            val response = Localhost.get("/")
            assertThat(response.statusCode).isEqualTo(INTERNAL_SERVER_ERROR_500)
        }
    }
}