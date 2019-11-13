package daikon


import daikon.Localhost.get
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HttpRoutingTest {

    @Test
    fun `multiple routes`() {
        HttpServer()
            .get("/foo") { _, res -> res.write("Hello foo") }
            .get("/bar") { _, res -> res.write("Bye bar") }
            .start().use {
                assertThat(get("/foo").text).isEqualTo("Hello foo")
                assertThat(get("/bar").text).isEqualTo("Bye bar")
            }
    }

    @Test
    fun `route starts with`() {
        HttpServer()
            .get("/*") { _, res -> res.write("Hello foo") }
            .get("/bar/*") { _, res -> res.write("Bye bar") }
            .start().use {
                assertThat(get("/ba").text).isEqualTo("Hello foo")
                assertThat(get("/bar/").text).isEqualTo("Bye bar")
            }
    }

    @Test
    fun `serve static files`() {
        HttpServer()
            .assets("/foo/*")
            .start().use {
                assertThat(get("/foo/style.css").text).isEqualTo("body {}")
            }
    }

    @Test
    fun `mix of static file and dynamic routes`() {
        HttpServer()
            .get("/bar/2") {_, res -> res.write("Hello")}
            .assets("/foo/*")
            .start().use {
                assertThat(get("/foo/style.css").text).isEqualTo("body {}")
                assertThat(get("/bar/2").text).isEqualTo("Hello")
            }
    }

    @Test
    fun `custom port`() {
        HttpServer(4546)
            .get("/*") {_, res -> res.write("Hello")}
            .start().use {
                assertThat(khttp.get("http://localhost:4546/").text).isEqualTo("Hello")
            }
    }
}