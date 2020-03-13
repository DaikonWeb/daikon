package daikon

import com.nhaarman.mockitokotlin2.mock
import daikon.core.Method.*
import daikon.core.Route
import daikon.core.RouteAction
import daikon.core.Routing
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RoutingTest {

    @Test
    fun `filter by method`() {
        val routing = Routing()
            .add(Route(GET, "/", NopAction()))
            .add(Route(POST, "/", NopAction()))

        val (method, _, _) = routing.bestFor(POST, "/")

        assertThat(method).isEqualTo(POST)
    }

    @Test
    fun `filter by exact path`() {
        val routing = Routing()
            .add(Route(GET, "/foo", NopAction()))
            .add(Route(GET, "/bar", NopAction()))

        val (_, path, _) = routing.bestFor(GET, "/bar")

        assertThat(path).isEqualTo("/bar")
    }

    @Test
    fun `manage any method`() {
        val routing = Routing().add(Route(ANY, "/foo", NopAction()))

        val (_, path, _) = routing.bestFor(GET, "/foo")

        assertThat(path).isEqualTo("/foo")
    }

    @Test
    fun `route starts with`() {
        val routing = Routing()
            .add(Route(GET, "/foo*", NopAction()))
            .add(Route(GET, "/*", NopAction()))

        assertThat(routing.bestFor(GET, "/").path).isEqualTo("/*")
        assertThat(routing.bestFor(GET, "/foo").path).isEqualTo("/foo*")
        assertThat(routing.bestFor(GET, "/fo").path).isEqualTo("/*")
        assertThat(routing.bestFor(GET, "/foobar").path).isEqualTo("/foo*")
        assertThat(routing.bestFor(GET, "/foo/bar").path).isEqualTo("/foo*")
    }

    @Test
    fun `use strongest route`() {
        val routing = Routing()
            .add(Route(GET, "/*", NopAction()))
            .add(Route(GET, "/bar/*", NopAction()))

        assertThat(routing.bestFor(GET, "/ba").path).isEqualTo("/*")
        assertThat(routing.bestFor(GET, "/bar/").path).isEqualTo("/bar/*")
    }

    @Test
    fun `default route`() {
        val routing = Routing()
            .add(Route(GET, "/", NopAction()))
            .default(Route(ANY, "/", NopAction()))

        assertThat(routing.bestFor(POST, "/").method).isEqualTo(ANY)
    }

    @Test
    fun `all route for a given path preserve order`() {
        val routes = listOf(
            Route(GET, "/*", NopAction()),
            Route(GET, "/foo/*", NopAction()),
            Route(GET, "/foo/bar", NopAction())
        )

        val all = Routing().add(routes[0]).add(routes[1]).add(routes[2]).allFor(GET, "/foo/bar")

        assertThat(all).isEqualTo(routes)
    }

    @Test
    fun `override route`() {
        val routeActionA: RouteAction = mock()
        val routeActionB: RouteAction = mock()
        val routing = Routing()
            .add(Route(GET, "/", routeActionA))
            .add(Route(GET, "/", routeActionB))

        assertThat(routing.bestFor(GET, "/").action).isEqualTo(routeActionB)
    }

    @Test
    fun `use most specific route`() {
        val routeActionA: RouteAction = mock()
        val routeActionB: RouteAction = mock()
        val routing = Routing()
            .add(Route(GET, "/", routeActionA))
            .add(Route(GET, "/*", routeActionB))

        assertThat(routing.bestFor(GET, "/").action).isEqualTo(routeActionA)
    }
}
