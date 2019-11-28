package daikon

import daikon.Method.ANY
import org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404
import org.eclipse.jetty.http.HttpStatus.OK_200
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
        val httpRes = HttpResponse(res as HttpServletResponse)

        try {
            befores
                .allFor(Method.valueOf(req.method), req.requestURI)
                .forEach { invoke(it, req, httpRes) }

            routes
                .default(Route(ANY, "ignore", DefaultRouteAction()))
                .bestFor(Method.valueOf(req.method), req.requestURI)
                .also { invoke(it, req, httpRes) }

            afters
                .allFor(Method.valueOf(req.method), req.requestURI)
                .forEach { invoke(it, req, httpRes) }
        }catch (e: HaltException) {
        }
    }

    private fun invoke(route: Route, req: HttpServletRequest, res: HttpResponse) {
        route.action.handle(HttpRequest(req, PathParams(route.path)), res)
    }
}
