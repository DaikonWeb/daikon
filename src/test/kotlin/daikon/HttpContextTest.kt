package daikon

import daikon.Localhost.get
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HttpContextTest {

    @Test
    fun `context preserve attributes`() {
        HttpServer()
            .afterStart { ctx -> ctx.addAttribute("key", "value") }
            .get("/") { _, res, ctx -> res.write(ctx.getAttribute("key")) }
            .start().use {
                assertThat(get("/").text).isEqualTo("value")
            }
    }
}