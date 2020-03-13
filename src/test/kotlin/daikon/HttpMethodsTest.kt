package daikon

import daikon.core.HttpStatus.ACCEPTED_202
import daikon.core.HttpStatus.NOT_FOUND_404
import daikon.core.HttpStatus.NO_CONTENT_204
import daikon.core.HttpStatus.OK_200
import daikon.Localhost.delete
import daikon.Localhost.get
import daikon.Localhost.head
import daikon.Localhost.options
import daikon.Localhost.post
import daikon.Localhost.put
import org.assertj.core.api.Assertions.assertThat
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
    fun `page not found`() {
        HttpServer()
            .get("/", NopAction())
            .start().use {
                assertThat(post("/").statusCode).isEqualTo(NOT_FOUND_404)
                assertThat(get("/foo").statusCode).isEqualTo(NOT_FOUND_404)
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

    @Test
    fun `do put`() {
        HttpServer()
            .put("/") { _, res -> res.status(NO_CONTENT_204) }
            .start().use {
                assertThat(put("/").statusCode).isEqualTo(NO_CONTENT_204)
            }
    }

    @Test
    fun `do delete`() {
        HttpServer()
                .delete("/") { _, res -> res.status(NO_CONTENT_204) }
                .start().use {
                    assertThat(delete("/").statusCode).isEqualTo(NO_CONTENT_204)
                }
    }

    @Test
    fun `do options`() {
        HttpServer()
                .options("/") { _, res -> res.status(OK_200) }
                .start().use {
                    assertThat(options("/").statusCode).isEqualTo(OK_200)
                }
    }
}