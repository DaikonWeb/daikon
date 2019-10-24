package daikon

import khttp.get
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RoutingTest {

    @Test
    fun `multiple routes`() {
        HttpServer()
            .get("/foo") { _, res -> res.write("Hello foo") }
            .get("/bar") { _, res -> res.write("Bye bar") }
            .start().use {
                assertThat(get("http://localhost:4545/foo").text).isEqualTo("Hello foo")
                assertThat(get("http://localhost:4545/bar").text).isEqualTo("Bye bar")
            }
    }
}