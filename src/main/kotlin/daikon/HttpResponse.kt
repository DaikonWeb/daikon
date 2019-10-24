package daikon

import javax.servlet.http.HttpServletResponse

class HttpResponse(private val request: HttpServletResponse) : Response {

    override fun write(text: String) {
        request.writer.write(text)
    }
}