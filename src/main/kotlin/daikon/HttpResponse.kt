package daikon

import javax.servlet.http.HttpServletResponse

class HttpResponse(private val response: HttpServletResponse) : Response {
    override fun status(code: Int) {
        response.status = code
    }

    override fun write(text: String) {
        response.writer.write(text)
    }
}