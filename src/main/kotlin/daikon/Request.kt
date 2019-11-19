package daikon

interface Request {
    fun param(name: String): String?
    fun header(name: String): String?
    fun body(): String
    fun url(): String
    fun path(): String
    fun <T> attribute(key: String, value: T?)
    fun <T> attribute(key: String) : T?
}