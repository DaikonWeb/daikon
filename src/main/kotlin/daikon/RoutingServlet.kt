package daikon

import daikon.core.*
import javax.servlet.GenericServlet
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RoutingServlet(befores: Routing, routes: Routing, afters: Routing, context: Context, exceptions: Exceptions) : GenericServlet() {
    private val routingHandler = RoutingHandler(befores, routes, afters, context, exceptions)

    override fun service(servletRequest: ServletRequest, servletResponse: ServletResponse) {
        val request = HttpRequest(servletRequest as HttpServletRequest)
        val response = HttpResponse(servletResponse as HttpServletResponse)

        routingHandler.execute(request, response)
    }
}