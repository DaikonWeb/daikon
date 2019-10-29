package daikon

import daikon.Localhost.get
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ResponseTest {

    @Test
    fun `status code`() {
        HttpServer()
            .any("/") { _, res -> res.status(201) }
            .start().use {
                assertThat(get("/").statusCode).isEqualTo(201)
            }
    }

    @Test
    fun `content type`() {
        HttpServer()
            .any("/") { _, res -> res.content("application/json") }
            .start().use {
                assertThat(get("/").headers["Content-Type"]).isEqualTo("application/json")
            }
    }
}