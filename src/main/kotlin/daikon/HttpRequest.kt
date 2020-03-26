package daikon

import daikon.core.Method
import daikon.core.PathParams
import daikon.core.Request
import java.net.URLDecoder.decode
import java.nio.charset.StandardCharsets.UTF_8
import javax.servlet.http.HttpServletRequest

class HttpRequest(private val request: HttpServletRequest) : Request {
    private val body by lazy { request.reader.readText() }
    private lateinit var pathParams: PathParams

    override fun withPathParams(value: String): HttpRequest {
        pathParams = PathParams(value)
        return this
    }

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
        return body
    }

    override fun header(name: String): String {
        return request.getHeader(name)
    }

    override fun hasHeader(name: String): Boolean {
        return request.headerNames.toList().contains(name)
    }

    override fun param(name: String): String {
         return getBodyParameter(name) ?: request.getParameter(name) ?: pathParams.valueOf(path()).getValue(name)
    }

    private fun getBodyParameter(name: String): String? {
        if(body().isEmpty() || !hasHeader("Content-Type") || !header("Content-Type").startsWith("application/x-www-form-urlencoded")) {
            return null
        }
        try {
            return body().split("&").map {
                val (key, value) = it.split("=")
                key to decode(value, UTF_8.name())
            }.toMap()[name]
        }catch (t: Throwable) {
            return null
        }
    }
}