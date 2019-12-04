package daikon

import org.eclipse.jetty.http.HttpStatus.MOVED_TEMPORARILY_302

interface Response {
    fun write(text: String)
    fun status(code: Int)
    fun content(type: String)
    fun header(name: String, value: String)
    fun body(): String
    fun redirect(path: String, status: Int = MOVED_TEMPORARILY_302)
}