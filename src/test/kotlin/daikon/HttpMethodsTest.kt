package daikon

import daikon.core.HttpStatus.ACCEPTED_202
import daikon.core.HttpStatus.NOT_FOUND_404
import daikon.core.HttpStatus.NO_CONTENT_204
import daikon.core.HttpStatus.OK_200
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import topinambur.http

class HttpMethodsTest {

    @Test
    fun `do get`() {
        HttpServer()
            .get("/") { _, res -> res.write("Hello") }
            .start().use {
                assertThat(local("/").http.get().body).isEqualTo("Hello")
            }
    }

    @Test
    fun `do post`() {
        HttpServer()
            .post("/") { _, res -> res.write("Hello") }
            .start().use {
                assertThat(local("/").http.post().body).isEqualTo("Hello")
            }
    }

    @Test
    fun `do any`() {
        HttpServer()
            .any("/") { _, res -> res.write("Hello") }
            .start().use {
                assertThat(local("/").http.get().body).isEqualTo("Hello")
                assertThat(local("/").http.post().body).isEqualTo("Hello")
            }
    }

    @Test
    fun `page not found`() {
        HttpServer()
            .get("/", NopAction())
            .start().use {
                assertThat(local("/").http.post().statusCode).isEqualTo(NOT_FOUND_404)
                assertThat(local("/foo").http.get().statusCode).isEqualTo(NOT_FOUND_404)
            }
    }

    @Test
    fun `do head`() {
        HttpServer()
            .head("/") { _, res -> res.status(ACCEPTED_202) }
            .start().use {
                assertThat(local("/").http.head().statusCode).isEqualTo(ACCEPTED_202)
            }
    }

    @Test
    fun `do put`() {
        HttpServer()
            .put("/") { _, res -> res.status(NO_CONTENT_204) }
            .start().use {
                assertThat(local("/").http.call("PUT").statusCode).isEqualTo(NO_CONTENT_204)
            }
    }

    @Test
    fun `do delete`() {
        HttpServer()
                .delete("/") { _, res -> res.status(NO_CONTENT_204) }
                .start().use {
                    assertThat(local("/").http.delete().statusCode).isEqualTo(NO_CONTENT_204)
                }
    }

    @Test
    fun `do options`() {
        HttpServer()
                .options("/") { _, res -> res.status(OK_200) }
                .start().use {
                    assertThat(local("/").http.call("OPTIONS").statusCode).isEqualTo(OK_200)
                }
    }
}