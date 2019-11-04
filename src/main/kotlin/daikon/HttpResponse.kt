package daikon

import javax.servlet.http.HttpServletResponse

class HttpResponse(private val response: HttpServletResponse) : Response {
    override fun header(name: String, value: String) {
        response.addHeader(name, value)
    }

    override fun content(type: String) {
       response.contentType = type
    }

    override fun status(code: Int) {
        response.status = code
    }

    override fun write(text: String) {
        response.writer.write(text)
    }
}