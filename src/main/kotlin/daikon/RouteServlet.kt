package daikon

import daikon.Method.GET
import daikon.Method.POST
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RouteServlet(private val method: Method, private val action: (Request, Response) -> Unit) : HttpServlet() {

    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        execute(POST, request, response)
    }

    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        execute(GET, request, response)
    }

    private fun execute(actualMethod: Method, request: HttpServletRequest, response: HttpServletResponse) {
        when(method){
            actualMethod -> action.invoke(HttpRequest(request), HttpResponse(response))
            else -> notAllowed(response)
        }
    }

    private fun notAllowed(response: HttpServletResponse) {
        response.status = 405
    }
}
