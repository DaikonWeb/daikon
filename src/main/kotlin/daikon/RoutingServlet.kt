package daikon

import javax.servlet.GenericServlet
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RoutingServlet(befores: Routing, routes: Routing, afters: Routing, context: Context) : GenericServlet() {
    private val routingHandler = RoutingHandler(befores, routes, afters, context)

    override fun service(servletRequest: ServletRequest, servletResponse: ServletResponse) {
        routingHandler.execute(
            HttpRequest(servletRequest as HttpServletRequest),
            HttpResponse(servletResponse as HttpServletResponse)
        )
    }
}
