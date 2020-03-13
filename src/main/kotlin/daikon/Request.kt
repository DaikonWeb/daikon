package daikon

interface Request {
    fun param(name: String): String
    fun header(name: String): String
    fun hasHeader(name: String): Boolean
    fun body(): String
    fun url(): String
    fun path(): String
    fun <T> attribute(key: String, value: T)
    fun <T> attribute(key: String) : T
    fun method(): Method
    fun withPathParams(value: String): Request
}