package daikon

import daikon.Localhost.get
import daikon.Localhost.post
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RequestTest {

    @Test
    fun `query string parameter`() {
        HttpServer()
            .get("/") { req, res -> res.write("hello ${req.param("name")}") }
            .start().use {
                assertThat(get("/?name=Bob").text).isEqualTo("hello Bob")
            }
    }

    @Test
    fun `post data`() {
        HttpServer()
            .post("/*") { req, res -> res.write("hello ${req.param("name")}") }
            .start()
            .use {
                assertThat(post("/", data = mapOf("name" to "Bob")).text).isEqualTo("hello Bob")
            }
    }

    @Test
    fun headers() {
        HttpServer()
            .post("/*") { req, res -> res.write("hello ${req.header("name")}") }
            .start()
            .use {
                assertThat(post("/", headers = mapOf("name" to "Bob")).text).isEqualTo("hello Bob")
            }
    }

    @Test
    fun body() {
        HttpServer()
            .any("/") { req, res -> res.write("Hello ${req.body()}") }
            .start().use {
                assertThat(post("/", data = "Bob").text).isEqualTo("Hello Bob")
            }
    }
}