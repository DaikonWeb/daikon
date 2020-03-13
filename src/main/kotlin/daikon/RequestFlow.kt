package daikon

import daikon.HttpStatus.INTERNAL_SERVER_ERROR_500

object RequestFlow {
    fun halt(response: Response, statusCode: Int = INTERNAL_SERVER_ERROR_500, message: String = "") {
        response.status(statusCode)
        response.write(message)
        throw HaltException()
    }
}
