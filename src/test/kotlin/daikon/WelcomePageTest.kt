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

}