package daikon

import daikon.Method.ANY
import javax.servlet.GenericServlet
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RoutingServlet(
    private val befores: Routing,
    private val routes: Routing,
    private val afters: Routing,
    private val context: Context
) : GenericServlet() {

    override fun service(servletRequest: ServletRequest, servletResponse: ServletResponse) {
        val request = HttpRequest(servletRequest as HttpServletRequest)
        val response = HttpResponse(servletResponse as HttpServletResponse)

        try {
            befores
                .allFor(request.method(), request.uri())
                .forEach { invoke(it, request, response) }

            routes
                .default(Route(ANY, "ignore", DefaultRouteAction()))
                .bestFor(request.method(), request.uri())
                .also { invoke(it, request, response) }

            afters
                .allFor(request.method(), request.uri())
                .forEach { invoke(it, request, response) }
        }catch (e: HaltException) {
        }
    }

    private fun invoke(route: Route, req: HttpRequest, res: HttpResponse) {
        route.action.handle(req.withPathParams(route.path), res, context)
    }
}
