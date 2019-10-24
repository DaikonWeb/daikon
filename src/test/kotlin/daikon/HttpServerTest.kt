package daikon

import org.junit.jupiter.api.Test
import khttp.get
import org.assertj.core.api.Assertions.assertThat

class HttpServerTest {

    @Test
    fun `start and stop`() {
        HttpServer()
            .get("/") { _, _ -> }
            .start().use {
                val response = get("http://localhost:4545/")
                assertThat(response.statusCode).isEqualTo(200)
            }
    }
}