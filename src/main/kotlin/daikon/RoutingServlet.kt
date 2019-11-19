package daikon

import daikon.Method.ANY
import org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404
import javax.servlet.GenericServlet
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RoutingServlet(
    private val befores: Routing,
    private val routes: Routing,
    private val afters: Routing
) : GenericServlet() {

    override fun service(req: ServletRequest, res: ServletResponse) {
        req as HttpServletRequest
        res as HttpServletResponse

        befores
            .allFor(Method.valueOf(req.method), req.requestURI)
            .forEach { invoke(it, req, res) }

        routes
            .default(Route(ANY, "/*", DummyRouteAction { _, r -> r.status(NOT_FOUND_404) }))
            .bestFor(Method.valueOf(req.method), req.requestURI)
            .also { invoke(it, req, res) }

        afters
            .allFor(Method.valueOf(req.method), req.requestURI)
            .forEach { invoke(it, req, res) }
    }

    private fun invoke(route: Route, req: HttpServletRequest, res: HttpServletResponse) {
        route.action.handle(HttpRequest(req, PathParams(route.path)), HttpResponse(res))
    }
}
