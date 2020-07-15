package daikon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import topinambur.http
import java.net.URLEncoder.encode
import java.nio.charset.StandardCharsets.UTF_8

class CharacterEncodingTest {

    @Test
    fun `body supports utf8`() {
        HttpServer()
            .get("/") { _, res -> res.write("%&è") }
            .start().use {
                assertThat(local("/").http.get().body).isEqualTo("%&è")
            }
    }

    @Test
    fun `query string parameter supports utf8`() {
        HttpServer()
            .get("/") { req, res -> res.write(req.param("name")) }
            .start().use {
                assertThat(local("/?name=è$%26").http.get().body).isEqualTo("è$&")
            }
    }

    @Test
    fun `path parameters supports utf8`() {
        HttpServer()
            .get("/:name") { req, res -> res.write(req.param(":name")) }
            .start().use {
                assertThat(local("/è%24%26").http.get().body).isEqualTo(encode("è$&", UTF_8.name()))
            }
    }

    @Test
    fun `post data supports utf8`() {
        HttpServer()
            .post("/") { req, res -> res.write(req.param("name")) }
            .start().use {
                val response = local("/").http.post(data = mapOf("name" to "è$&"))
                assertThat(response.body).isEqualTo("è$&")
            }
    }

    @Test
    fun `post body supports utf8`() {
        HttpServer()
            .post("/") { req, res -> res.write(req.body()) }
            .start().use {
                val response = local("/").http.post(body = "è$&")
                assertThat(response.body).isEqualTo("è$&")
            }
    }

    @Test
    fun `header does not supports utf8`() {
        HttpServer()
            .get("/") { req, res -> res.write(req.header("name")) }
            .start().use {
                val response = local("/").http.get(headers = mapOf("name" to "è$&"))
                assertThat(response.body).isEqualTo("Ã¨$&")
            }
    }
}