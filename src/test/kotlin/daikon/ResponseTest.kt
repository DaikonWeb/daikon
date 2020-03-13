package daikon

import daikon.Localhost.get
import org.assertj.core.api.Assertions.assertThat
import daikon.core.HttpStatus.CREATED_201
import daikon.core.HttpStatus.MOVED_PERMANENTLY_301
import org.junit.jupiter.api.Test

class ResponseTest {

    @Test
    fun `status code`() {
        HttpServer()
            .any("/") { _, res -> res.status(CREATED_201) }
            .start().use {
                assertThat(get("/").statusCode).isEqualTo(CREATED_201)
            }
    }

    @Test
    fun `content type`() {
        HttpServer()
            .any("/") { _, res -> res.type("application/json") }
            .start().use {
                assertThat(get("/").headers["Content-Type"]).isEqualTo("application/json")
            }
    }

    @Test
    fun headers() {
        HttpServer()
            .any("/") { _, res -> res.header("foo", "bar") }
            .start().use {
                assertThat(get("/").headers["foo"]).isEqualTo("bar")
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
                get("/")
                assertThat(body).isEqualTo("Hi Bob")
            }
    }

    @Test
    fun `redirect to relative path`() {
        HttpServer()
            .any("/foo") { _, res -> res.redirect("/bar", MOVED_PERMANENTLY_301) }
            .any("/bar") { _, res -> res.write("Hello") }
            .start().use {
                assertThat(get("/foo").text).isEqualTo("Hello")
            }
    }

    @Test
    fun `redirect to absolute path`() {
        HttpServer()
            .any("/foo") { _, res -> res.redirect("http://localhost:4545/bar") }
            .any("/bar") { _, res -> res.write("Hello") }
            .start().use {
                assertThat(get("/foo").text).isEqualTo("Hello")
            }
    }
}