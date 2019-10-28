package daikon

interface Request {
    fun param(name: String): String
}