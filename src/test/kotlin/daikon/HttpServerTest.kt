package daikon

import daikon.Localhost.get
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import daikon.HttpStatus.OK_200

class HttpServerTest {

    @Test
    fun `start and stop`() {
        HttpServer()
            .get("/", NopAction())
            .start().use {
                val response = get("/")
                assertThat(response.statusCode).isEqualTo(OK_200)
            }
    }

    @Test
    fun `start and stop passing actions as block`() {
        HttpServer {
            get("/", NopAction())
        }.start().use {
            val response = get("/")
            assertThat(response.statusCode).isEqualTo(OK_200)
        }
    }
}