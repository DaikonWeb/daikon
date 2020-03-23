package daikon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import topinambur.http

class HttpContextTest {

    @Test
    fun `context preserve attributes`() {
        HttpServer()
            .afterStart { ctx -> ctx.addAttribute("key", "value") }
            .get("/") { _, res, ctx -> res.write(ctx.getAttribute("key")) }
            .start().use {
                assertThat(local("/").http.get().body).isEqualTo("value")
            }
    }
}