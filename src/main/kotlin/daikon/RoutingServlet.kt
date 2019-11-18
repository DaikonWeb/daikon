package daikon

import daikon.Method.ANY
import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.http.HttpStatus.METHOD_NOT_ALLOWED_405
import org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404
import org.eclipse.jetty.server.ResourceService
import org.eclipse.jetty.servlet.DefaultServlet
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
        val request = HttpRequest(req as HttpServletRequest)
        val response = HttpResponse(res as HttpServletResponse)

        befores
            .allFor(Method.valueOf(req.method.toUpperCase()), req.requestURI)
            .forEach { it.action.invoke(request, response) }

        routes
            .default(Route(ANY, "/*") { _, r -> r.status(NOT_FOUND_404) })
            .bestFor(Method.valueOf(req.method.toUpperCase()), req.requestURI)
            .action
            .invoke(request, response)

        afters
            .allFor(Method.valueOf(req.method.toUpperCase()), req.requestURI)
            .forEach { it.action.invoke(request, response) }
    }
}
