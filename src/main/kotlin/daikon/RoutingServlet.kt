package daikon

import daikon.core.*
import javax.servlet.GenericServlet
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RoutingServlet(private val routingHandler: RoutingHandler) : GenericServlet() {

    override fun service(servletRequest: ServletRequest, servletResponse: ServletResponse) {
        val request = HttpRequest(servletRequest as HttpServletRequest)
        val response = HttpResponse(servletResponse as HttpServletResponse)

        routingHandler.execute(request, response)
    }
}