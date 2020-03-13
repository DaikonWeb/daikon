package daikon

import daikon.core.HttpStatus.INTERNAL_SERVER_ERROR_500
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
    fun `post parameters`() {
        HttpServer()
            .post("/*") { req, res -> res.write(req.body()) }
            .start()
            .use {
                assertThat(post("/", data = mapOf("name" to "Bob")).text).isEqualTo("name=Bob")
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
                assertThat(post("/").statusCode).isEqualTo(INTERNAL_SERVER_ERROR_500)
            }
    }

    @Test
    fun `check if an header is present`() {
        HttpServer()
            .post("/*") { req, res ->
                val name = if (req.hasHeader("name")) req.header("name") else "World"
                res.write("Hello $name")
            }
            .start()
            .use {
                assertThat(post("/").text).isEqualTo("Hello World")
                assertThat(post("/", mapOf("name" to "Bob")).text).isEqualTo("Hello Bob")
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
    fun `access twice to the body`() {
        HttpServer()
            .before("/") { req, _ -> println("Body: ${req.body()}") }
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
            .get("/") { req, res -> res.write(req.param(":baz")) }
            .start().use {
                assertThat(get("/").statusCode).isEqualTo(INTERNAL_SERVER_ERROR_500)
            }
    }

    @Test
    fun `request url`() {
        HttpServer()
            .get("/:foo") { req, res -> res.write(req.url()) }
            .start().use {
                assertThat(get("/123").text).isEqualTo("http://localhost:4545/123")
            }
    }

    @Test
    fun `request path`() {
        HttpServer()
            .get("/:foo") { req, res -> res.write(req.path()) }
            .start().use {
                assertThat(get("/123").text).isEqualTo("/123")
            }
    }

    @Test
    fun attribute() {
        HttpServer()
            .before("/") { req, _ -> req.attribute("foo_key", "foo_value") }
            .get("/") { req, res -> res.write(req.attribute("foo_key")) }
            .start().use {
                assertThat(get("/").text).isEqualTo("foo_value")
            }
    }

    @Test
    fun `attribute not found`() {
        HttpServer()
            .get("/") { req, res -> res.write("Hello ${req.attribute<String>("any")}") }
            .start().use {
                assertThat(get("/").statusCode).isEqualTo(INTERNAL_SERVER_ERROR_500)
            }
    }

    @Test
    fun method() {
        HttpServer()
            .post("/*") { req, res -> res.write("${req.method()}") }
            .start()
            .use {
                assertThat(post("/").text).isEqualTo("POST")
            }
    }
}