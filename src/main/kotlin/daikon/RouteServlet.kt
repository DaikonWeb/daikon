package daikon

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RouteServlet(private val action: (Request, Response) -> Unit) : HttpServlet() {

    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        doGet(request, response)
    }

    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        action.invoke(HttpRequest(request), HttpResponse(response))
    }
}
