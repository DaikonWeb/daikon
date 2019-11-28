package daikon

import javax.servlet.http.HttpServletRequest

class HttpRequest(private val request: HttpServletRequest, private val pathParams: PathParams) : Request {
    override fun <T> attribute(key: String): T {
        @Suppress("UNCHECKED_CAST")
        return request.getAttribute(key)!! as T
    }

    override fun method(): Method {
        return Method.valueOf(request.method)
    }

    override fun <T> attribute(key: String, value: T) {
        request.setAttribute(key, value)
    }

    override fun path(): String {
        return request.requestURI
    }

    override fun url(): String {
        return request.requestURL.toString()
    }

    override fun body(): String {
        return request.reader.readText()
    }

    override fun header(name: String): String {
        return request.getHeader(name)
    }

    override fun param(name: String): String {
         return request.getParameter(name) ?: pathParams.valueOf(path()).getValue(name)
    }
}