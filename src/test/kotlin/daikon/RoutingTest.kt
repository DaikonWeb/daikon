package daikon

import daikon.Method.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RoutingTest {

    @Test
    fun `filter by method`() {
        val routing = Routing()
            .add(Route(GET, "/") { _, _ -> })
            .add(Route(POST, "/") { _, _ -> })

        val (method, _, _) = routing.bestFor(POST, "/")

        assertThat(method).isEqualTo(POST)
    }

    @Test
    fun `filter by exact path`() {
        val routing = Routing()
            .add(Route(GET, "/foo") { _, _ -> })
            .add(Route(GET, "/bar") { _, _ -> })

        val (_, path, _) = routing.bestFor(GET, "/bar")

        assertThat(path).isEqualTo("/bar")
    }

    @Test
    fun `manage any method`() {
        val routing = Routing().add(Route(ANY, "/foo") { _, _ -> })

        val (_, path, _) = routing.bestFor(GET, "/foo")

        assertThat(path).isEqualTo("/foo")
    }

    @Test
    fun `route starts with`() {
        val routing = Routing()
            .add(Route(GET, "/foo*") { _, _ -> })
            .add(Route(GET, "/*") { _, _ -> })

        assertThat(routing.bestFor(GET, "/").path).isEqualTo("/*")
        assertThat(routing.bestFor(GET, "/foo").path).isEqualTo("/foo*")
        assertThat(routing.bestFor(GET, "/fo").path).isEqualTo("/*")
        assertThat(routing.bestFor(GET, "/foobar").path).isEqualTo("/foo*")
        assertThat(routing.bestFor(GET, "/foo/bar").path).isEqualTo("/foo*")
    }

    @Test
    fun `use strongest route`() {
        val routing = Routing()
            .add(Route(GET, "/*") { _, _ -> })
            .add(Route(GET, "/bar/*") { _, _ -> })

        assertThat(routing.bestFor(GET, "/ba").path).isEqualTo("/*")
        assertThat(routing.bestFor(GET, "/bar/").path).isEqualTo("/bar/*")
    }

    @Test
    fun `default route`() {
        val routing = Routing()
            .add(Route(GET, "/") { _, _ -> })
            .default(Route(ANY, "/") { _, _ -> })

        assertThat(routing.bestFor(POST, "/").method).isEqualTo(ANY)
    }

    @Test
    fun `all route for a given path preserve order`() {
        val routes = listOf(
            Route(GET, "/*") { _, _ -> },
            Route(GET, "/foo/*") { _, _ -> },
            Route(GET, "/foo/bar") { _, _ -> }
        )

        val all = Routing().add(routes[0]).add(routes[1]).add(routes[2]).allFor(GET, "/foo/bar")

        assertThat(all).isEqualTo(routes)
    }
}