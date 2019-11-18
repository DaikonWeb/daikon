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
    fun `header not found`() {
        HttpServer()
            .post("/*") { req, res -> res.write("hello ${req.header("name")}") }
            .start()
            .use {
                assertThat(post("/").text).isEqualTo("hello null")
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

    @Test
    fun `empty body`() {
        HttpServer()
            .any("/") { req, res -> res.write("Foo${req.body()}Bar") }
            .start().use {
                assertThat(post("/").text).isEqualTo("FooBar")
            }
    }

    @Test
    fun `path parameter at the end of the path`() {
        HttpServer()
            .get("/foo/:size") { req, res -> res.write("He wears size ${req.param(":size")}") }
            .start().use {
                assertThat(get("/foo/XL").text).isEqualTo("He wears size XL")
            }
    }

    @Test
    fun `parameters not found`() {
        HttpServer()
            .get("/:foo") { req, res -> res.write("${req.param(":baz")} ${req.param("baz")}") }
            .start().use {
                assertThat(get("/123").text).isEqualTo("null null")
            }
    }
}