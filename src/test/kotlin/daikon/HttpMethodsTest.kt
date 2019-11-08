package daikon

import daikon.Localhost.get
import daikon.Localhost.head
import daikon.Localhost.post
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.http.HttpStatus.ACCEPTED_202
import org.eclipse.jetty.http.HttpStatus.METHOD_NOT_ALLOWED_405
import org.junit.jupiter.api.Test

class HttpMethodsTest {

    @Test
    fun `do get`() {
        HttpServer()
            .get("/") { _, res -> res.write("Hello") }
            .start().use {
                assertThat(get("/").text).isEqualTo("Hello")
            }
    }

    @Test
    fun `do post`() {
        HttpServer()
            .post("/") { _, res -> res.write("Hello") }
            .start().use {
                assertThat(post("/").text).isEqualTo("Hello")
            }
    }

    @Test
    fun `do any`() {
        HttpServer()
            .any("/") { _, res -> res.write("Hello") }
            .start().use {
                assertThat(get("/").text).isEqualTo("Hello")
                assertThat(post("/").text).isEqualTo("Hello")
            }
    }

    @Test
    fun `not allowed method`() {
        HttpServer()
            .get("/") { _, _ -> }
            .start().use {
                assertThat(post("/").statusCode).isEqualTo(METHOD_NOT_ALLOWED_405)
            }
    }

    @Test
    fun `do head`() {
        HttpServer()
            .head("/") { _, res -> res.status(ACCEPTED_202) }
            .start().use {
                assertThat(head("/").statusCode).isEqualTo(ACCEPTED_202)
            }
    }
}