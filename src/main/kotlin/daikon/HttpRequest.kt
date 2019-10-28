package daikon

import javax.servlet.http.HttpServletRequest

class HttpRequest(private val request: HttpServletRequest) : Request {

    override fun param(name: String): String {
        return request.getParameter(name)
    }
}