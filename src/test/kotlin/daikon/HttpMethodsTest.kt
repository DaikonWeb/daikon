package daikon

import khttp.get
import khttp.post
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HttpMethodsTest {

    @Test
    fun `do get`() {
        HttpServer()
            .get("/") { _, res -> res.write("Hello") }
            .start().use {
                assertThat(get("http://localhost:4545/").text).isEqualTo("Hello")
            }
    }

    @Test
    fun `do post`() {
        HttpServer()
            .post("/") { _, res -> res.write("Hello") }
            .start().use {
                assertThat(post("http://localhost:4545/").text).isEqualTo("Hello")
            }
    }

    @Test
    fun `do any`() {
        HttpServer()
            .any("/") { _, res -> res.write("Hello") }
            .start().use {
                assertThat(get("http://localhost:4545/").text).isEqualTo("Hello")
                assertThat(post("http://localhost:4545/").text).isEqualTo("Hello")
            }
    }

    @Test
    fun `not allowed method`() {
        HttpServer()
            .get("/") { _, _ -> }
            .start().use {
                assertThat(post("http://localhost:4545/").statusCode).isEqualTo(405)
            }
    }
}