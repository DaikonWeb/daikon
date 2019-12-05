package daikon

import daikon.Localhost.get
import daikon.Localhost.post
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

class CharacterEncodingTest {

    @Test
    fun `body supports utf8`() {
        HttpServer()
            .get("/") { _, res -> res.write("%&è") }
            .start().use {
                assertThat(get("/").text).isEqualTo("%&è")
            }
    }

    @Test
    fun `query string parameter supports utf8`() {
        HttpServer()
            .get("/") { req, res -> res.write(req.param("name")) }
            .start().use {
                assertThat(get("/?name=è$%26").text).isEqualTo("è$&")
            }
    }

    @Test
    fun `path parameters supports utf8`() {
        HttpServer()
            .get("/:name") { req, res -> res.write(req.param(":name")) }
            .start().use {
                assertThat(get("/è%24%26").text).isEqualTo(URLEncoder.encode("è$&", UTF_8.name()))
            }
    }

    @Test
    fun `post data supports utf8`() {
        HttpServer()
            .post("/") { req, res -> res.write(req.param("name")) }
            .start().use {
                val response = post("/", data = mapOf("name" to "è$&"))
                assertThat(response.text).isEqualTo("è$&")
            }
    }

    @Test
    fun `header does not supports utf8`() {
        HttpServer()
            .get("/") { req, res -> res.write(req.header("name")) }
            .start().use {
                val response = get("/", headers = mapOf("name" to "è$&"))
                assertThat(response.text).isEqualTo("Ã¨$&")
            }
    }
}