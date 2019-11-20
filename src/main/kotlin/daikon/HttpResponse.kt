package daikon

import java.io.StringWriter
import javax.servlet.http.HttpServletResponse

class HttpResponse(private val response: HttpServletResponse) : Response {
    private val writer = StringWriter()

    override fun header(name: String, value: String) {
        response.addHeader(name, value)
    }

    override fun body(): String {
        return writer.toString()
    }

    override fun content(type: String) {
       response.contentType = type
    }

    override fun status(code: Int) {
        response.status = code
    }

    override fun write(text: String) {
        writer.write(text)
        response.writer.write(text)
    }
}