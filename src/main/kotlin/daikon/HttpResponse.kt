package daikon

import daikon.core.Response
import java.io.StringWriter
import java.nio.charset.StandardCharsets.UTF_8
import javax.servlet.http.HttpServletResponse

class HttpResponse(private val response: HttpServletResponse) : Response {
    private val writer = StringWriter()
    init {
        response.addHeader("Server", "Daikon")
    }

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

    override fun type() = response.contentType

    override fun status(code: Int) {
        response.status = code
    }

    override fun status() = response.status

    override fun write(text: String) {
        writer.write(text)
        write(text.toByteArray(UTF_8))
    }

    override fun write(byteArray: ByteArray) {
        response.outputStream.write(byteArray)
    }
}