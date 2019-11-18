package daikon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PathParamsTest {

    @Test
    fun `replace placeholder with stars`() {
        val pathParams = PathParams("/foo/:one/bar/:two/baz")

        assertThat(pathParams.path()).isEqualTo("/foo/*/bar/*/baz")
    }

    @Test
    fun `extract parameters`() {
        val pathParams = PathParams("/foo/:one/bar/:two/baz")

        assertThat(pathParams.valueOf("/foo/1/bar/2/baz")[":one"]).isEqualTo("1")
    }

    @Test
    fun `value of not found parameter is null`() {
        val pathParams = PathParams("/:baz")

        assertThat(pathParams.valueOf("/123")[":bar"]).isNull()
    }
}