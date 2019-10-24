package daikon

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RouteServlet(private val route: (Request, Response) -> Unit) : HttpServlet() {

    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        route.invoke(HttpRequest(request), HttpResponse(response))
    }
}
