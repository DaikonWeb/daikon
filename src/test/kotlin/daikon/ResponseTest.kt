package daikon

import daikon.core.HttpStatus.CREATED_201
import daikon.core.HttpStatus.MOVED_PERMANENTLY_301
import daikon.core.HttpStatus.NOT_FOUND_404
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import topinambur.http

class ResponseTest {

    @Test
    fun `status code`() {
        HttpServer()
            .any("/") { _, res -> res.status(CREATED_201) }
            .start().use {
                assertThat(local("/").http.get().statusCode).isEqualTo(CREATED_201)
            }
    }

    @Test
    fun `content type`() {
        HttpServer()
            .any("/") { _, res -> res.type("application/json") }
            .start().use {
                assertThat(local("/").http.get().header("Content-Type")).isEqualTo("application/json")
            }
    }

    @Test
    fun headers() {
        HttpServer()
            .any("/") { _, res -> res.header("foo", "bar") }
            .start().use {
               assertThat(local("/").http.get().headers["foo"]).isEqualTo("bar")
            }
    }

    @Test
    fun body() {
        var body = ""

        HttpServer()
            .before("/") { _, res -> res.write("Hi") }
            .any("/") { _, res ->
                res.write(" Bob")
                body = res.body()
            }
            .start().use {
                local("/").http.get()
                assertThat(body).isEqualTo("Hi Bob")
            }
    }

    @Test
    fun `redirect to relative path`() {
        HttpServer()
            .any("/foo") { _, res -> res.redirect("/bar", MOVED_PERMANENTLY_301) }
            .any("/bar") { _, res -> res.write("Hello") }
            .start().use {
                assertThat(local("/foo").http.get().body).isEqualTo("Hello")
            }
    }

    @Test
    fun `redirect to absolute path`() {
        HttpServer()
            .any("/foo") { _, res -> res.redirect("http://localhost:4545/bar") }
            .any("/bar") { _, res -> res.write("Hello") }
            .start().use {
                assertThat(local("/foo").http.get().body).isEqualTo("Hello")
            }
    }

    @Test
    fun `override Server header`() {
        HttpServer()
            .any("/") { _, _ -> }
            .start().use {
                assertThat(local("/").http.get().header("Server")).isEqualTo("Daikon")
            }
    }

    @Test
    fun `can access to response status code`() {
        HttpServer()
            .any("/") { _, res -> res.status(NOT_FOUND_404)}
            .after("/") { _, res -> res.write("StatusCode = ${res.status()}")}
            .start().use {
                assertThat(local("/").http.get().body).isEqualTo("StatusCode = 404")
            }
    }

    @Test
    fun `can access to response type`() {
        HttpServer()
            .any("/") { _, res -> res.type("application/json")}
            .after("/") { _, res -> res.write("Type = ${res.type()}")}
            .start().use {
                assertThat(local("/").http.get().body).isEqualTo("Type = application/json")
            }
    }
}