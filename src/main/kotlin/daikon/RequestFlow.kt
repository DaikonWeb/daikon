package daikon

import org.eclipse.jetty.http.HttpStatus.INTERNAL_SERVER_ERROR_500

object RequestFlow {
    fun halt(statusCode: Int = INTERNAL_SERVER_ERROR_500, message: String = "") {
        throw HaltException(statusCode, message)
    }
}
