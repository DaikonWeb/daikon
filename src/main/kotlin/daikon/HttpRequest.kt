package daikon

import javax.servlet.http.HttpServletRequest

class HttpRequest(private val request: HttpServletRequest) : Request {

    override fun header(name: String): String {
        return request.getHeader(name)
    }

    override fun param(name: String): String {
        return request.getParameter(name)
    }
}