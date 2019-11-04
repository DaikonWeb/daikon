package daikon

interface Response {
    fun write(text: String)
    fun status(code: Int)
    fun content(type: String)
    fun header(name: String, value: String)
}