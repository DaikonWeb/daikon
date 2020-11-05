package daikon

import daikon.core.Method
import daikon.core.Part
import daikon.core.PathParams
import daikon.core.Request
import java.net.URLDecoder.decode
import java.nio.charset.StandardCharsets.UTF_8
import javax.servlet.http.HttpServletRequest

class HttpRequest(private val request: HttpServletRequest) : Request {
    private val body by lazy { request.inputStream.readBytes() }
    private val multipartBody by lazy { request.parts }
    private lateinit var pathParams: PathParams

    override fun withPathParams(value: String): HttpRequest {
        pathParams = PathParams(value)
        return this
    }

    override fun <T> attribute(key: String): T {
        @Suppress("UNCHECKED_CAST")
        return request.getAttribute(key)!! as T
    }

    override fun hasAttribute(key: String) = request.attributeNames.toList().contains(key)

    override fun method() = Method.valueOf(request.method)

    override fun <T> attribute(key: String, value: T) = request.setAttribute(key, value)

    override fun path(): String = request.requestURI

    override fun url() = request.requestURL.toString() + if(request.queryString.isNullOrEmpty()) "" else "?${request.queryString}"

    override fun body() = body.toString(UTF_8)

    override fun multipart(name: String): Part {
        val part = multipartBody.single { it.name == name }
        val file = part.inputStream.readBytes()
        val partName = part.submittedFileName ?: part.name

        return Part(partName, part.contentType, file)
    }

    override fun header(name: String): String = request.getHeader(name)

    override fun hasHeader(name: String) = request.headerNames.toList().contains(name)

    override fun param(name: String): String {
         return getBodyParameter(name) ?: request.getParameter(name) ?: pathParams.valueOf(path()).getValue(name)
    }

    override fun hasParam(name: String) = try { param(name); true } catch (t: Throwable) { false }

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