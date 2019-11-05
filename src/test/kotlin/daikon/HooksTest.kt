package daikon

import daikon.Localhost.get
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jetty.http.HttpStatus.ACCEPTED_202
import org.junit.jupiter.api.Test

class HooksTest {

    @Test
    fun `do action before calling route`() {
        HttpServer()
            .before("/") { _, res -> res.write("Hello") }
            .get("/") { _, res -> res.status(ACCEPTED_202)}
            .start().use {
                val response = get("/")
                assertThat(response.text).isEqualTo("Hello")
                assertThat(response.statusCode).isEqualTo(ACCEPTED_202)
            }
    }
}