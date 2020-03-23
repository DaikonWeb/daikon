package daikon

import daikon.core.HttpStatus.ACCEPTED_202
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import topinambur.http

class HooksTest {

    @Test
    fun `do action before calling route`() {
        HttpServer()
            .before("/") { _, res -> res.write("Hello") }
            .get("/") { _, res -> res.status(ACCEPTED_202)}
            .start().use {
                val response = local("/").http.get()
                assertThat(response.body).isEqualTo("Hello")
                assertThat(response.statusCode).isEqualTo(ACCEPTED_202)
            }
    }

    @Test
    fun `do action after route was called`() {
        HttpServer()
            .get("/") { _, res -> res.write("Hello")}
            .after { _, res -> res.write(" world") }
            .start().use {
                val response = local("/").http.get()
                assertThat(response.body).isEqualTo("Hello world")
            }
    }

    @Test
    fun `do action before route was called`() {
        HttpServer()
            .before { _, res -> res.write("Hello") }
            .get("/") { _, res -> res.write(" world")}
            .start().use {
                val response = local("/").http.get()
                assertThat(response.body).isEqualTo("Hello world")
            }
    }

    @Test
    fun `do multiple action before calling route`() {
        HttpServer()
            .before("/*") { _, res -> res.write("B") }
            .before("/foo/*") { _, res -> res.write("y") }
            .before("/foo/bar") { _, res -> res.write("e") }
            .get("/*") { _, res -> res.write(" Bob")}
            .start().use {
                val response = local("/foo/bar").http.get()
                assertThat(response.body).isEqualTo("Bye Bob")
            }
    }

    @Test
    fun `before supports path`() {
        HttpServer()
            .path("/baz") {
                before("/bar") { _, res -> res.write("Hello") }
            }
            .get("/baz/bar") { _, res -> res.write(" Bob") }
            .start().use {
                val response = local("/baz/bar").http.get()
                assertThat(response.body).isEqualTo("Hello Bob")
            }
    }

    @Test
    fun `after supports path`() {
        HttpServer()
            .path("/baz") {
                after("/bar") { _, res -> res.write(" Bob") }
            }
            .get("/baz/bar") { _, res -> res.write("Hello") }
            .start().use {
                val response = local("/baz/bar").http.get()
                assertThat(response.body).isEqualTo("Hello Bob")
            }
    }

    @Test
    fun `do action after start`() {
        var called = false
        HttpServer()
            .afterStart {
                assertThat(local("/").http.get().body).isEqualTo("Hello")
                called = true
            }
            .get("/") { _, res -> res.write("Hello")}
            .start()
            .close()

        assertThat(called).isTrue()
    }

    @Test
    fun `do action before stop`() {
        var called = false
        HttpServer()
            .beforeStop {
                assertThat(local("/").http.get().body).isEqualTo("Hello")
                called = true
            }
            .get("/") { _, res -> res.write("Hello")}
            .start()
            .close()

        assertThat(called).isTrue()
    }
}