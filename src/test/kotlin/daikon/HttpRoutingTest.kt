package daikon


import daikon.core.Context
import daikon.core.HttpStatus.INTERNAL_SERVER_ERROR_500
import daikon.core.HttpStatus.NOT_FOUND_404
import daikon.core.HttpStatus.OK_200
import daikon.core.HttpStatus.UNAUTHORIZED_401
import daikon.core.Request
import daikon.core.RequestFlow.halt
import daikon.core.Response
import daikon.core.RouteAction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import topinambur.http

class HttpRoutingTest {

    @Test
    fun `multiple routes`() {
        HttpServer()
            .get("/foo") { _, res -> res.write("Hello foo") }
            .get("/bar") { _, res -> res.write("Bye bar") }
            .start().use {
                assertThat(local("/foo").http.get().body).isEqualTo("Hello foo")
                assertThat(local("/bar").http.get().body).isEqualTo("Bye bar")
            }
    }

    @Test
    fun `route starts with`() {
        HttpServer()
            .get("/*") { _, res -> res.write("Hello foo") }
            .get("/bar/*") { _, res -> res.write("Bye bar") }
            .start().use {
                assertThat(local("/ba").http.get().body).isEqualTo("Hello foo")
                assertThat(local("/bar/").http.get().body).isEqualTo("Bye bar")
            }
    }

    @Test
    fun `serve static files`() {
        HttpServer()
            .assets("/foo/*")
            .get("/bar/2") { _, res -> res.write("Hello") }
            .start().use {
                assertThat(local("/foo/style.css").http.get().header("Content-Type")).isEqualTo("text/css")
                assertThat(local("/foo/style.css").http.get().body).isEqualTo("body {}")
                assertThat(local("/bar/2").http.get().body).isEqualTo("Hello")
            }
    }

    @Test
    fun `custom port`() {
        HttpServer(4546)
            .get("/*") { _, res -> res.write("Hello") }
            .start().use {
                assertThat("http://localhost:4546/".http.get().body).isEqualTo("Hello")
            }
    }

    @Test
    fun `route action`() {
        HttpServer()
            .get("/foo", object : RouteAction {
                override fun handle(request: Request, response: Response, context: Context) {
                    response.write("Hello foo")
                }
            })
            .start().use {
                assertThat(local("/foo").http.get().body).isEqualTo("Hello foo")
            }
    }

    @Test
    fun `halt request`() {
        HttpServer()
            .before { _, res ->
                halt(res, UNAUTHORIZED_401, "Go away")
            }
            .get("/") { _, res ->
                res.status(OK_200)
            }
            .start().use {
                val response = local("/").http.get()
                assertThat(response.statusCode).isEqualTo(UNAUTHORIZED_401)
                assertThat(response.body).isEqualTo("Go away")
            }
    }

    @Test
    fun `nested paths`() {
        HttpServer()
            .path("/a") {
                get("/b") { _, res -> res.write("ab") }
                get("/c") { _, res -> res.write("ac") }
                path("/d") {
                    get("/e") { _, res -> res.write("ade") }
                }
            }
            .get("/f") { _, res -> res.write("f") }
            .start().use {
                assertThat(local("/a/b").http.get().body).isEqualTo("ab")
                assertThat(local("/a/c").http.get().body).isEqualTo("ac")
                assertThat(local("/a/d/e").http.get().body).isEqualTo("ade")
                assertThat(local("/f").http.get().body).isEqualTo("f")
            }
    }

    @Test
    fun `path params`() {
        HttpServer {
            get("/:foo") { req, res -> res.write("Hello ${req.param(":foo")}") }
        }.start().use {
            assertThat(local("/a").http.get().body).isEqualTo("Hello a")
            assertThat(local("/a/b").http.get().statusCode).isEqualTo(NOT_FOUND_404)
        }
    }

    @Test
    fun `error handling`() {
        HttpServer {
            get("/foo") { _, _ -> throw RuntimeException() }
            get("/bar") { _, res -> res.write("Hello")}
        }.start().use {
            assertThat(local("/foo").http.get().statusCode).isEqualTo(INTERNAL_SERVER_ERROR_500)
            assertThat(local("/bar").http.get().body).isEqualTo("Hello")
        }
    }
}