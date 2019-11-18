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
        val response = HttpResponse(res as HttpServletResponse)
        val method = (req as HttpServletRequest).method.toUpperCase()
        
        befores
            .allFor(Method.valueOf(method), req.requestURI)
            .forEach { it.action.invoke(httpRequest(req, PathParams(it.path)), response) }

        val bestOne = routes
            .default(Route(ANY, "/*") { _, r -> r.status(NOT_FOUND_404) })
            .bestFor(Method.valueOf(method), req.requestURI)
        bestOne.action.invoke(httpRequest(req, PathParams(bestOne.path)), response)

        afters
            .allFor(Method.valueOf(method), req.requestURI)
            .forEach { it.action.invoke(httpRequest(req, PathParams(it.path)), response) }
    }

    private fun httpRequest(req: ServletRequest, pathParams: PathParams) = HttpRequest(req as HttpServletRequest, pathParams)
}
