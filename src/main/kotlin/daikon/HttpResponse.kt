package daikon

import java.io.StringWriter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.UTF_8
import javax.servlet.http.HttpServletResponse

class HttpResponse(private val response: HttpServletResponse) : Response {
    private val writer = StringWriter()

    override fun header(name: String, value: String) {
        response.addHeader(name, value)
    }

    override fun body(): String {
        return writer.toString()
    }

    override fun redirect(path: String, status: Int) {
        status(status)
        header("Location", path)
    }

    override fun type(contentType: String) {
       response.contentType = contentType
    }

    override fun status(code: Int) {
        response.status = code
    }

    override fun write(text: String) {
        writer.write(text)
        response.characterEncoding = UTF_8.name()
        response.writer.write(text)
    }
}