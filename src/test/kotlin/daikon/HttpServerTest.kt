package daikon

import daikon.core.HttpStatus.INTERNAL_SERVER_ERROR_500
import daikon.core.HttpStatus.OK_200
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import topinambur.http
import java.lang.RuntimeException

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

    @Test
    fun `default response when throws an exception`() {
        HttpServer {
            get("/") { _, _ -> throw RuntimeException("Something") }
        }.start().use {
            val response = local("/").http.get()
            assertThat(response.statusCode).isEqualTo(INTERNAL_SERVER_ERROR_500)
            assertThat(response.body).contains("Something")
            println(response.body)
        }
    }

    @Test
    fun `override response when throws an exception`() {
        HttpServer {
            exception(RuntimeException::class.java) { _, res, _ ->
                res.status(OK_200)
                res.write("OK")
            }
            get("/") { _, _ -> throw RuntimeException("Something") }
        }.start().use {
            val response = local("/").http.get()
            assertThat(response.statusCode).isEqualTo(OK_200)
            assertThat(response.body).contains("OK")
        }
    }
}