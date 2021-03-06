package daikon

import daikon.core.HttpStatus.INTERNAL_SERVER_ERROR_500
import daikon.core.Request
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import topinambur.FieldPart
import topinambur.FilePart
import topinambur.http

class RequestTest {

    @Test
    fun `has parameter`() {
        HttpServer()
            .get("/") { req, _ ->
                assertThat(req.hasParam("name")).isTrue()
                assertThat(req.hasParam("age")).isFalse()
            }
            .start().use {
                local("/?name=Bob").http.get()
            }
    }

    @Test
    fun `query string parameter`() {
        HttpServer()
            .get("/") { req, res -> res.write("hello ${req.param("name")}") }
            .start().use {
                assertThat(local("/?name=Bob").http.get().body).isEqualTo("hello Bob")
            }
    }

    @Test
    fun `post data`() {
        HttpServer()
            .post("/*") { req, res -> res.write("hello ${req.param("name")}") }
            .start()
            .use {
                assertThat(local("/").http.post(data = mapOf("name" to "Bob")).body).isEqualTo("hello Bob")
            }
    }

    @Test
    fun `post parameters`() {
        HttpServer()
            .post("/*") { req, res -> res.write(req.body()) }
            .start()
            .use {
                assertThat(local("/").http.post(data = mapOf("name" to "Bob")).body).isEqualTo("name=Bob")
            }
    }

    @Test
    fun `read param in post with empty body`() {
        HttpServer()
            .post("/:test") { req, res -> res.write(req.param(":test")) }
            .start()
            .use {
                assertThat(local("/any").http.post().body).isEqualTo("any")
            }
    }

    @Test
    fun `can read post parameters and body`() {
        HttpServer()
            .post("/") { req, res -> res.write("""${req.body()} - ${req.param("name")}""") }
            .start()
            .use {
                assertThat(local("/").http.post(data = mapOf("name" to "Bob")).body).isEqualTo("name=Bob - Bob")
            }
    }

    @Test
    fun headers() {
        HttpServer()
            .post("/*") { req, res -> res.write("hello ${req.header("name")}") }
            .start()
            .use {
                assertThat(local("/").http.post(headers = mapOf("name" to "Bob")).body).isEqualTo("hello Bob")
            }
    }

    @Test
    fun `header not found`() {
        HttpServer()
            .post("/*") { req, res -> res.write("hello ${req.header("name")}") }
            .start()
            .use {
                assertThat(local("/").http.post().statusCode).isEqualTo(INTERNAL_SERVER_ERROR_500)
            }
    }

    @Test
    fun `check if an header is present`() {
        HttpServer()
            .post("/*") { req, _ ->
                assertThat(req.hasHeader("name")).isTrue()
                assertThat(req.hasHeader("age")).isFalse()
            }
            .start()
            .use {
                local("/").http.post(headers = mapOf("name" to "Bob"))
            }
    }

    @Test
    fun body() {
        HttpServer()
            .any("/") { req, res -> res.write("Hello ${req.body()}") }
            .start().use {
                assertThat(local("/").http.post(body = "Bob").body).isEqualTo("Hello Bob")
            }
    }

    @Test
    fun `access twice to the body`() {
        HttpServer()
            .before("/") { req, _ -> println("Body: ${req.body()}") }
            .any("/") { req, res -> res.write("Hello ${req.body()}") }
            .start().use {
                assertThat(local("/").http.post(body = "Bob").body).isEqualTo("Hello Bob")
            }
    }

    @Test
    fun `empty body`() {
        HttpServer()
            .any("/") { req, res -> res.write("Foo${req.body()}Bar") }
            .start().use {
                assertThat(local("/").http.post().body).isEqualTo("FooBar")
            }
    }

    @Test
    fun `path parameter at the end of the path`() {
        HttpServer()
            .get("/foo/:size") { req, res -> res.write("He wears size ${req.param(":size")}") }
            .start().use {
                assertThat(local("/foo/XL").http.get().body).isEqualTo("He wears size XL")
            }
    }

    @Test
    fun `parameters not found`() {
        HttpServer()
            .get("/") { req, res -> res.write(req.param(":baz")) }
            .start().use {
                assertThat(local("/").http.get().statusCode).isEqualTo(INTERNAL_SERVER_ERROR_500)
            }
    }

    @Test
    fun `request url`() {
        HttpServer()
            .get("/:foo") { req, res -> res.write(req.url()) }
            .start().use {
                assertThat(local("/123").http.get().body).isEqualTo("http://localhost:4545/123")
            }
    }

    @Test
    fun `request url with querystring`() {
        HttpServer()
                .get("/test") { req, res -> res.write(req.url()) }
                .start().use {
                    assertThat(local("/test?name=Bob&surname=Marley").http.get().body).isEqualTo("http://localhost:4545/test?name=Bob&surname=Marley")
                }
    }

    @Test
    fun `request path`() {
        HttpServer()
            .get("/:foo") { req, res -> res.write(req.path()) }
            .start().use {
                assertThat(local("/123").http.get().body).isEqualTo("/123")
            }
    }

    @Test
    fun attribute() {
        HttpServer()
            .before("/") { req, _ -> req.attribute("foo_key", "foo_value") }
            .get("/") { req, res -> res.write(req.attribute<String>("foo_key")) }
            .start().use {
                assertThat(local("/").http.get().body).isEqualTo("foo_value")
            }
    }

    @Test
    fun `has attribute`() {
        HttpServer()
            .before("/") { req, _ -> req.attribute("foo", "any") }
            .get("/") { req, _ ->
                assertThat(req.hasAttribute("foo")).isTrue()
                assertThat(req.hasAttribute("bar")).isFalse()
            }
            .start().use {
                local("/").http.get()
            }
    }

    @Test
    fun `attribute not found`() {
        HttpServer()
            .get("/") { req, res -> res.write("Hello ${req.attribute<String>("any")}") }
            .start().use {
                assertThat(local("/").http.get().statusCode).isEqualTo(INTERNAL_SERVER_ERROR_500)
            }
    }

    @Test
    fun method() {
        HttpServer()
            .post("/*") { req, res -> res.write("${req.method()}") }
            .start()
            .use {
                assertThat(local("/").http.post().body).isEqualTo("POST")
            }
    }

    @Test
    fun `receive a file`() {
        HttpServer()
            .post("/*") { req: Request, res ->
                val file = req.multipart("firstData")

                res.write(file.content)
            }
            .start()
            .use {
                val response = local("/").http.post(
                        data = mapOf("firstData" to FilePart("file", "txt", "testo da convertire".toByteArray()))
                )

                assertThat(response.body).isEqualTo("testo da convertire")
            }
    }

    @Test
    fun `receive a file and a field`() {
        HttpServer()
            .post("/*") { req: Request, res ->
                val file = req.multipart("firstData")
                val field = req.multipart("secondData")

                res.write("""${file.content.toString(Charsets.UTF_8)} | ${field.content.toString(Charsets.UTF_8)}""")
            }
            .start()
            .use {
                val response = local("/").http.post(
                        data = mapOf(
                            "firstData" to FilePart("file", "txt", "testo da convertire".toByteArray()),
                            "secondData" to FieldPart("valore")
                        )
                )

                assertThat(response.body).isEqualTo("testo da convertire | valore")
            }
    }
}