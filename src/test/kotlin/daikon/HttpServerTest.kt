package daikon

import daikon.core.HttpStatus.OK_200
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import topinambur.http

class HttpServerTest {

    @Test
    fun `start and stop`() {
        HttpServer()
            .get("/", NopAction())
            .start().use {
                val response = local("/").http.get()
                assertThat(response.statusCode).isEqualTo(OK_200)
            }
    }

    @Test
    fun `start and stop passing actions as block`() {
        HttpServer {
            get("/", NopAction())
        }.start().use {
            val response = local("/").http.get()
            assertThat(response.statusCode).isEqualTo(OK_200)
        }
    }
}