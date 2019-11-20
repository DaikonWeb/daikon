package daikon

data class HaltException(val statusCode: Int, val bodyMesage: String) : Throwable(bodyMesage)
